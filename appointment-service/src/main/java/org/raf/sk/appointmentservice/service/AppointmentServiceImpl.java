package org.raf.sk.appointmentservice.service;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.client.notification.AppointmentReservationDto;
import org.raf.sk.appointmentservice.client.notification.NotificationMQ;
import org.raf.sk.appointmentservice.client.user.AppointmentUserDto;
import org.raf.sk.appointmentservice.client.user.ManagerDto;
import org.raf.sk.appointmentservice.domain.*;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.hall.CreateHallDto;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.raf.sk.appointmentservice.dto.hall.UpdateHallDto;
import org.raf.sk.appointmentservice.dto.reservation.CreateReservationDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.listener.MessageHelper;
import org.raf.sk.appointmentservice.mapper.AppointmentMapper;
import org.raf.sk.appointmentservice.mapper.HallMapper;
import org.raf.sk.appointmentservice.mapper.ReservationMapper;
import org.raf.sk.appointmentservice.repository.AppointmentRepository;
import org.raf.sk.appointmentservice.repository.HallRepository;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.raf.sk.appointmentservice.repository.TrainingRepository;
import org.raf.sk.appointmentservice.security.tokenService.TokenService;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.raf.sk.appointmentservice.constants.Constants.*;

@Service
@Transactional
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final HallRepository hallRepository;
    private final ReservationRepository reservationRepository;
    private final AppointmentRepository appointmentRepository;
    private final TrainingRepository trainingRepository;
    private final HallMapper hallMapper;
    private final ReservationMapper reservationMapper;
    private final AppointmentMapper appointmentMapper;
    private final TokenService tokenService;
    private RestTemplate userServiceRestTemplate;
    private final JmsTemplate jmsTemplate;
    private final MessageHelper messageHelper;

    @Override
    public Response<Page<HallDto>> findAllHalls(Pageable pageable) {
        return new Response<>(STATUS_OK, "All halls", hallRepository.findAll(pageable).map(hallMapper::hallToHallDto));
    }

    @Override
    public Response<HallDto> findHallById(Long hallId) {
        return hallRepository.getHallById(hallId)
                .map(hall -> new Response<>(STATUS_OK, "Hall found", hallMapper.hallToHallDto(hall)))
                .orElse(new Response<>(STATUS_NOT_FOUND, "Hall not found", null));
    }

    @Override
    public Response<Boolean> createHall(String jwt, CreateHallDto createHallDto) {
        if (tokenService.getRole(jwt).equals("MANAGER")) {
            Hall hall = hallMapper.createHallDtoToHall(createHallDto);
            hallRepository.save(hall);
            return new Response<>(STATUS_OK, "Hall created", true);
        }
        return new Response<>(STATUS_FORBIDDEN, "Forbidden", false);
    }

    @Override
    public Response<Boolean> updateHall(String jwt, UpdateHallDto updateHallDto) {
        if (tokenService.getRole(jwt).equals("MANAGER")) {
            return hallRepository.getHallById(updateHallDto.getId())
                    .map(hall -> {
                        Optional.ofNullable(updateHallDto.getName()).ifPresent(hall::setName);
                        Optional.ofNullable(updateHallDto.getDescription()).ifPresent(hall::setDescription);
                        Optional.of(updateHallDto.getCoaches()).ifPresent(hall::setCoaches);
                        hallRepository.save(hall);
                        return new Response<>(STATUS_OK, "Hall updated", true);
                    })
                    .orElse(new Response<>(STATUS_NOT_FOUND, "Hall not found", false));
        }
        return new Response<>(STATUS_FORBIDDEN, "Forbidden", false);
    }

    @Override
    public Response<Boolean> deleteHall(String jwt, Long hallId) {
        if (tokenService.getRole(jwt).equals("MANAGER")) {
            hallRepository.deleteById(hallId);
            return new Response<>(STATUS_OK, "Hall deleted", true);
        }
        return new Response<>(STATUS_FORBIDDEN, "Forbidden", false);
    }

    @Override
    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Response<Boolean> scheduleReservation(AppointmentDto appointmentDto) {
        return Optional.ofNullable(fetchUserData(appointmentDto.getClientId()))
                .map(user -> {
                    Training training = trainingRepository.findById(appointmentDto.getTrainingId()).orElse(null);
                    Hall hall = training.getHall();

                    sendNotification(user, hall, training);
                    createReservation(appointmentDto, user, training);
                    updateAppointment(appointmentDto);

                    return new Response<>(STATUS_OK, "Reservation created", true);
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", false));
    }

    private AppointmentUserDto fetchUserData(Long clientId) {
        ResponseEntity<Response<AppointmentUserDto>> response;
        try {
            response = userServiceRestTemplate.exchange("user/getAppointmentUserData/" + clientId,
                    HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            return Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception ignored) {
            return null;
        }
    }

    private void sendNotification(AppointmentUserDto user, Hall hall, Training training) {
        String hallName = hall.getName();
        int price = user.getTotalSessions() % 10 == 0 ? 0 : (int) training.getPrice();
        String managerEmail = getManagerEmail(hall.getManagerId());

        AppointmentReservationDto reservationDto = new AppointmentReservationDto(managerEmail, user.getEmail(), user.getFirstName(), user.getLastName(), hallName, price);
        NotificationMQ<AppointmentReservationDto> msg = new NotificationMQ<>("RESERVATION", reservationDto);
        jmsTemplate.convertAndSend("send_emails", messageHelper.createTextMessage(msg));
    }

    private void createReservation(AppointmentDto appointmentDto, AppointmentUserDto user, Training training) {
        CreateReservationDto createReservationDto = reservationMapper.appointmentDtoToCreateReservationDto(appointmentDto);
        Reservation reservation = reservationMapper.createReservationDtoToReservation(createReservationDto);
        reservationRepository.save(reservation);
    }

    private void updateAppointment(AppointmentDto appointmentDto) {
        Appointment appointment = appointmentRepository.findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(
                        appointmentDto.getDate(),
                        appointmentDto.getStartTime(),
                        appointmentDto.getEndTime(),
                        appointmentDto.getTrainingId())
                .orElse(null);

        if (appointment != null) {
            appointment.setCurrentClients(appointment.getCurrentClients() + 1);
            if (appointment.getMaxClients() == appointment.getCurrentClients()) {
                appointment.setOpen(false);
            }
            appointmentRepository.save(appointment);
        }
    }

    @Override
    public Response<Boolean> cancelReservation(ReservationDto reservationDto) {
        Reservation reservation = reservationMapper.reservationDtoToReservation(reservationDto);
        reservationRepository.delete(reservation);
        Appointment appointment = appointmentRepository.findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(
                reservation.getDate(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getTraining().getId())
                .orElse(null);

        if (appointment != null) {
            appointment.setCurrentClients(appointment.getCurrentClients() - 1);
            appointment.setOpen(true);
            appointmentRepository.save(appointment);
        }

        return new Response<>(STATUS_OK, "Reservation canceled", true);
    }

    private String getManagerEmail(Long managerId) {
        ResponseEntity<Response<ManagerDto>> response;
        try {
            response = userServiceRestTemplate.exchange("user/getManagerData/" + managerId,
                    HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            ManagerDto user = Objects.requireNonNull(response.getBody()).getData();
            return user.getEmail();
        } catch (Exception ignored) {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public Response<Page<AppointmentDto>> findAllAppointments(Pageable pageable) {
        return new Response<>(STATUS_OK, "All appointments", appointmentRepository.findAll(pageable).map(appointmentMapper::appointmentToAppointmentDto));
    }

    @Override
    public Response<Page<AppointmentDto>> findAppointmentByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findAll(pageable);
        List<Appointment> filteredAppointments = page.getContent().stream()
                .filter(filter)
                .collect(Collectors.toList());

        Page<Appointment> filteredPage = new PageImpl<>(filteredAppointments, pageable, filteredAppointments.size());

        if (filteredAppointments.isEmpty())
            return new Response<>(STATUS_NOT_FOUND, "Appointments not found", null);

        return new Response<>(STATUS_OK, "Appointments found", filteredPage.map(appointmentMapper::appointmentToAppointmentDto));
    }

    @Override
    public Response<Page<ReservationDto>> findAllReservations(Pageable pageable) {
        return new Response<>(STATUS_OK, "All reservations", reservationRepository.findAll(pageable).map(reservationMapper::reservationToReservationDto));
    }

    @Override
    public Response<Page<ReservationDto>> findReservationByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        Page<Reservation> page = reservationRepository.findAll(pageable);
        List<Reservation> filteredReservations = page.getContent().stream()
                .filter(filter)
                .collect(Collectors.toList());

        Page<Reservation> filteredPage = new PageImpl<>(filteredReservations, pageable, filteredReservations.size());

        if (filteredReservations.isEmpty())
            return new Response<>(STATUS_NOT_FOUND, "Reservations not found", null);

        return new Response<>(STATUS_OK, "Reservations found", filteredPage.map(reservationMapper::reservationToReservationDto));
    }


}
