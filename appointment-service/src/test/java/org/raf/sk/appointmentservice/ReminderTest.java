package org.raf.sk.appointmentservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.raf.sk.appointmentservice.client.user.AppointmentUserDto;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.domain.Training;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.raf.sk.appointmentservice.service.Response;
import org.raf.sk.appointmentservice.service.services.ScheduleHandler;
import org.raf.sk.appointmentservice.util.ObjectMother;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyObject;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ReminderTest {

    @Autowired
    private ScheduleHandler scheduleHandler;

    @MockBean
    private ReservationRepository reservationRepository;
    @MockBean
    private JmsTemplate jmsTemplate;
    @MockBean
    private RestTemplate userServiceRestTemplate;

    @Test
    public void reminder_ZeroIteration() {
        // Arrange
        List<Reservation> reservations = List.of();
        Mockito.when(reservationRepository.findReservationsStartingIn24Hours()).thenReturn(reservations);
        Mockito.doNothing().when(jmsTemplate).convertAndSend(anyString(), Optional.ofNullable(anyObject()));

        // Act
        scheduleHandler.reminder();

        // Assert
        Mockito.verify(reservationRepository, Mockito.times(1)).findReservationsStartingIn24Hours();
        Mockito.verify(jmsTemplate, Mockito.times(0)).convertAndSend(anyString(), Optional.ofNullable(anyObject()));
    }

    @Test
    public void reminder_OneIterationUserNull() {
        // Arrange
        Reservation r1 = new Reservation("2022-12-09", 12, 14, "MONDAY", 3L);
        List<Reservation> reservations = List.of(r1);
        Mockito.when(reservationRepository.findReservationsStartingIn24Hours()).thenReturn(reservations);

        Mockito.when(userServiceRestTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.<ParameterizedTypeReference<Response<AppointmentUserDto>>>any()
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        Mockito.doNothing().when(jmsTemplate).convertAndSend(anyString(), Optional.ofNullable(anyObject()));

        // Act
        scheduleHandler.reminder();

        // Assert
        Mockito.verify(reservationRepository, Mockito.times(1)).findReservationsStartingIn24Hours();
        Mockito.verify(jmsTemplate, Mockito.times(0)).convertAndSend(anyString(), Optional.ofNullable(anyObject()));
    }

    @Test
    public void reminder_NIterationAllUserNull() {
        // Arrange
        Reservation r1 = new Reservation("2022-12-09", 12, 14, "MONDAY", 3L);
        Reservation r2 = new Reservation("2022-12-10", 12, 14, "MONDAY", 4L);
        Reservation r3 = new Reservation("2022-12-11", 12, 14, "MONDAY", 5L);
        List<Reservation> reservations = List.of(r1, r2, r3);
        Mockito.when(reservationRepository.findReservationsStartingIn24Hours()).thenReturn(reservations);

        Mockito.when(userServiceRestTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.<ParameterizedTypeReference<Response<AppointmentUserDto>>>any()
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        Mockito.doNothing().when(jmsTemplate).convertAndSend(anyString(), Optional.ofNullable(anyObject()));

        // Act
        scheduleHandler.reminder();

        // Assert
        Mockito.verify(reservationRepository, Mockito.times(1)).findReservationsStartingIn24Hours();
        Mockito.verify(jmsTemplate, Mockito.times(0)).convertAndSend(anyString(), Optional.ofNullable(anyObject()));
    }

    @Test
    public void reminder_OneIterationUserCorrect() {
        // Arrange
        Reservation r1 = new Reservation("2022-12-09", 12, 14, "MONDAY", 3L);
        Training t1 = new Training("Calisthenics", true, 1600);
        Hall h1 = new Hall("Ahiley 1", "/", 2, 1L);
        t1.setHall(h1);
        r1.setTraining(t1);
        List<Reservation> reservations = List.of(r1);
        Mockito.when(reservationRepository.findReservationsStartingIn24Hours()).thenReturn(reservations);

        AppointmentUserDto userDto = ObjectMother.createAppointmentUserDto();

        Response<AppointmentUserDto> response = new Response<>();
        response.setStatusCode(200);
        response.setMessage("Success");
        response.setData(userDto);

        Mockito.when(userServiceRestTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.<ParameterizedTypeReference<Response<AppointmentUserDto>>>any()
        )).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        Mockito.doNothing().when(jmsTemplate).convertAndSend(anyString(), Optional.ofNullable(anyObject()));

        // Act
        scheduleHandler.reminder();

        // Assert
        Mockito.verify(reservationRepository, Mockito.times(1)).findReservationsStartingIn24Hours();
        Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(anyString(), Optional.ofNullable(anyObject()));
    }

}
