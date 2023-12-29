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
import java.util.stream.Collectors;

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
    private RestTemplate userServiceRestTemplate;
    private final JmsTemplate jmsTemplate;
    private final MessageHelper messageHelper;

    @Override
    public Response<Page<HallDto>> findAllHalls(Pageable pageable) {
        return new Response<>(200, "All halls", hallRepository.findAll(pageable).map(hallMapper::hallToHallDto));
    }

    @Override
    public Response<HallDto> findHallById(Long hallId) {
        return hallRepository.getHallById(hallId)
                .map(hallMapper::hallToHallDto)
                .map(hallDto -> new Response<>(200, "Hall found", hallDto))
                .orElseGet(() -> new Response<>(404, "Hall not found", null));
    }

    @Override
    public Response<Boolean> createHall(String jwt, CreateHallDto createHallDto) {
        return null;
    }

    @Override
    public Response<Boolean> updateHall(String jwt, UpdateHallDto updateHallDto) {
        return null;
    }

    @Override
    public Response<Boolean> deleteHall(String jwt, Long hallId) {
        return null;
    }

    @Override
    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Response<Boolean> scheduleReservation(AppointmentDto appointmentDto) {
        ResponseEntity<Response<AppointmentUserDto>> response;
        AppointmentUserDto user;

        try {
            response = userServiceRestTemplate.exchange("user/getAppointmentUserData/" + appointmentDto.getClientId(),
                    HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            user = Objects.requireNonNull(response.getBody()).getData();
        } catch (Exception ignored) {
            return new Response<>(404, "User not found", false);
        }

        Training training = trainingRepository.findById(appointmentDto.getTrainingId()).orElse(null);
        Hall hall = training.getHall();

        String hallName = hall.getName();
        int price = user.getTotalSessions() % 10 == 0 ? 0 : (int) training.getPrice();
        String managerEmail = getManagerEmail(hall.getManagerId());

        AppointmentReservationDto reservationDto = new AppointmentReservationDto(managerEmail, user.getEmail(), user.getFirstName(), user.getLastName(), hallName, price);
        NotificationMQ<AppointmentReservationDto> msg = new NotificationMQ<>("RESERVATION", reservationDto);
        jmsTemplate.convertAndSend("send_emails", messageHelper.createTextMessage(msg));

        CreateReservationDto createReservationDto = reservationMapper.appointmentDtoToCreateReservationDto(appointmentDto);
        Reservation reservation = reservationMapper.createReservationDtoToReservation(createReservationDto);
        reservationRepository.save(reservation);

        Appointment appointment = appointmentRepository.findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(
                        reservation.getDate(),
                        reservation.getStartTime(),
                        reservation.getEndTime(),
                        reservation.getTraining().getId())
                .orElse(null);

        if (appointment != null) {
            appointment.setCurrentClients(appointment.getCurrentClients() + 1);
            if (appointment.getMaxClients() == appointment.getCurrentClients()) {
                appointment.setOpen(false);
            }
            appointmentRepository.save(appointment);
        }

        return new Response<>(200, "Reservation created", true);
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

        return new Response<>(200, "Reservation canceled", true);
    }

    public String getManagerEmail(Long managerId) {
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
        return new Response<>(200, "All appointments", appointmentRepository.findAll(pageable).map(appointmentMapper::appointmentToAppointmentDto));
    }

    @Override
    public Response<Page<AppointmentDto>> findAppointmentByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findAll(pageable);
        List<Appointment> filteredAppointments = page.getContent().stream()
                .filter(filter)
                .collect(Collectors.toList());

        Page<Appointment> filteredPage = new PageImpl<>(filteredAppointments, pageable, filteredAppointments.size());

        if (filteredAppointments.isEmpty())
            return new Response<>(404, "Appointments not found", null);

        return new Response<>(200, "Appointments found", filteredPage.map(appointmentMapper::appointmentToAppointmentDto));
    }

    @Override
    public Response<Page<ReservationDto>> findAllReservations(Pageable pageable) {
        return new Response<>(200, "All reservations", reservationRepository.findAll(pageable).map(reservationMapper::reservationToReservationDto));
    }

    @Override
    public Response<Page<ReservationDto>> findReservationByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        Page<Reservation> page = reservationRepository.findAll(pageable);
        List<Reservation> filteredReservations = page.getContent().stream()
                .filter(filter)
                .collect(Collectors.toList());

        Page<Reservation> filteredPage = new PageImpl<>(filteredReservations, pageable, filteredReservations.size());

        if (filteredReservations.isEmpty())
            return new Response<>(404, "Reservations not found", null);

        return new Response<>(200, "Reservations found", filteredPage.map(reservationMapper::reservationToReservationDto));
    }


}
