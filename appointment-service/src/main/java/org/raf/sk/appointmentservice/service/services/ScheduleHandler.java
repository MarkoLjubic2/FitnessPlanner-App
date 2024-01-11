package org.raf.sk.appointmentservice.service.services;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.client.notification.AppointmentReservationDto;
import org.raf.sk.appointmentservice.client.notification.NotificationMQ;
import org.raf.sk.appointmentservice.client.notification.ReminderDto;
import org.raf.sk.appointmentservice.client.user.AppointmentUserDto;
import org.raf.sk.appointmentservice.client.user.ManagerDto;
import org.raf.sk.appointmentservice.domain.Appointment;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.domain.Training;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.reservation.CreateReservationDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.listener.MessageHelper;
import org.raf.sk.appointmentservice.mapper.ReservationMapper;
import org.raf.sk.appointmentservice.repository.AppointmentRepository;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.raf.sk.appointmentservice.repository.TrainingRepository;
import org.raf.sk.appointmentservice.security.tokenService.TokenService;
import org.raf.sk.appointmentservice.service.Response;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.raf.sk.appointmentservice.constants.Constants.*;

@Component
@AllArgsConstructor
public class ScheduleHandler {

    private final TrainingRepository trainingRepository;
    private final ReservationRepository reservationRepository;
    private final AppointmentRepository appointmentRepository;
    private final ReservationMapper reservationMapper;
    private final RestTemplate userServiceRestTemplate;
    private final JmsTemplate jmsTemplate;
    private final MessageHelper messageHelper;
    private final TokenService tokenService;

    public Response<Boolean> scheduleReservation(AppointmentDto appointmentDto) {
        return Optional.ofNullable(fetchUserData(appointmentDto.getClientId()))
                .map(user -> {
                    Training training = trainingRepository.findById(appointmentDto.getTrainingId()).orElse(null);
                    if (training == null) return new Response<>(STATUS_NOT_FOUND, "Training not found", false);
                    Hall hall = training.getHall();

                    sendReservationNotification(user, hall, training);
                    createReservation(appointmentDto);
                    updateAppointment(appointmentDto);
                    changeTotalSessions(user.getId(), 1);

                    return new Response<>(STATUS_OK, "Reservation created", true);
                })
                .orElse(new Response<>(STATUS_NOT_FOUND, "User not found", false));
    }

    public Response<Boolean> cancelReservation(String jwt, ReservationDto reservationDto) {
        Reservation reservation = reservationMapper.reservationDtoToReservation(reservationDto);
        Appointment appointment = findAppointmentByReservation(reservation);

        if (appointment == null) return new Response<>(STATUS_NOT_FOUND, "Appointment not found!", false);
        if (tokenService.getRole(jwt).equals("MANAGER"))
            return handleManagerCancellation(reservation, appointment);
        else if (tokenService.getRole(jwt).equals("USER")) return handleUserCancellation(reservation, appointment);
        else return new Response<>(STATUS_BAD_REQUEST, "Bad request", false);
    }

    private Appointment findAppointmentByReservation(Reservation reservation) {
        return appointmentRepository.findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(
                        reservation.getDate(),
                        reservation.getStartTime(),
                        reservation.getEndTime(),
                        reservation.getTraining().getId())
                .orElse(null);
    }

    private Response<Boolean> handleManagerCancellation(Reservation reservation, Appointment appointment) {
        appointment.setOpen(false);
        appointmentRepository.save(appointment);
        for (Reservation r : reservationRepository.findAll()) {
            if (isSameReservation(reservation, r)) {
                r.setCanceled(true);
                reservationRepository.save(r);
                AppointmentUserDto user = fetchUserData(reservation.getClientId());
                if (user == null) return new Response<>(STATUS_INTERNAL_SERVER_ERROR, "Error", false);
                sendCancelNotification(user, appointment.getTraining().getHall());
                changeTotalSessions(user.getId(), -1);
            }
        }
        return new Response<>(STATUS_OK, "Appointment closed", true);
    }

    private Response<Boolean> handleUserCancellation(Reservation reservation, Appointment appointment) {
        AppointmentUserDto user = fetchUserData(reservation.getClientId());
        if (user == null) return new Response<>(STATUS_INTERNAL_SERVER_ERROR, "Error", false);
        for (Reservation r : reservationRepository.findAll()) {
            if (isSameReservation(reservation, r)) {
                r.setCanceled(true);
                reservationRepository.save(r);
                sendCancelNotification(user, appointment.getTraining().getHall());
                changeTotalSessions(user.getId(), -1);
                break;
            }
        }
        appointment.setCurrentClients(appointment.getCurrentClients() - 1);
        if (appointment.getCurrentClients() < appointment.getMaxClients()) {
            appointment.setOpen(true);
        }
        appointmentRepository.save(appointment);
        return new Response<>(STATUS_OK, "Reservation canceled", true);
    }

    private boolean isSameReservation(Reservation reservation, Reservation r) {
        return r.getClientId().equals(reservation.getClientId()) &&
                r.getDate().equals(reservation.getDate()) &&
                r.getStartTime().equals(reservation.getStartTime()) &&
                r.getEndTime().equals(reservation.getEndTime()) &&
                r.getTraining().getId().equals(reservation.getTraining().getId()) &&
                !r.isCanceled();
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

    private void sendReservationNotification(AppointmentUserDto user, Hall hall, Training training) {
        String hallName = hall.getName();
        int price = user.getTotalSessions() % 10 == 0 ? 0 : (int) training.getPrice();
        String managerEmail = getManagerEmail(hall.getManagerId());

        AppointmentReservationDto reservationDto = new AppointmentReservationDto(managerEmail, user.getEmail(), user.getFirstName(), user.getLastName(), hallName, price);
        NotificationMQ<AppointmentReservationDto> msg = new NotificationMQ<>("RESERVATION", reservationDto);
        jmsTemplate.convertAndSend("send_emails", messageHelper.createTextMessage(msg));
    }

    private void sendCancelNotification(AppointmentUserDto user, Hall hall) {
        String hallName = hall.getName();

        AppointmentReservationDto reservationDto = new AppointmentReservationDto(user.getEmail(), "", user.getFirstName(), user.getLastName(), hallName, 0);
        NotificationMQ<AppointmentReservationDto> msg = new NotificationMQ<>("RESERVATION_CANCEL", reservationDto);
        jmsTemplate.convertAndSend("send_emails", messageHelper.createTextMessage(msg));
    }

    private void createReservation(AppointmentDto appointmentDto) {
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

    private void changeTotalSessions(Long userId, int value) {
        String url = "user/changeTotalSessions/" + userId + "?value=" + value;
        userServiceRestTemplate.exchange(url, HttpMethod.PUT, null, Void.class);
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

    @Scheduled(cron = "0 0 0 * * ?")
    public void reminder() {
        List<Reservation> reservations = reservationRepository.findReservationsStartingIn24Hours();

        for (Reservation reservation : reservations) {
            AppointmentUserDto user = fetchUserData(reservation.getClientId());
            if (user == null) continue;
            Training training = reservation.getTraining();
            Hall hall = training.getHall();

            ReminderDto notificationDto = new ReminderDto(user.getEmail(), user.getFirstName(), user.getLastName(), hall.getName());
            NotificationMQ<ReminderDto> msg = new NotificationMQ<>("REMINDER", notificationDto);
            jmsTemplate.convertAndSend("send_emails", messageHelper.createTextMessage(msg));

        }
    }

}
