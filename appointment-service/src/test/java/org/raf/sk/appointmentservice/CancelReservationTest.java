package org.raf.sk.appointmentservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.raf.sk.appointmentservice.client.user.AppointmentUserDto;
import org.raf.sk.appointmentservice.domain.Appointment;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.mapper.ReservationMapper;
import org.raf.sk.appointmentservice.repository.AppointmentRepository;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.raf.sk.appointmentservice.security.tokenService.TokenService;
import org.raf.sk.appointmentservice.service.Response;
import org.raf.sk.appointmentservice.service.services.ScheduleHandler;
import org.raf.sk.appointmentservice.util.ObjectMother;
import org.raf.sk.appointmentservice.util.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_NOT_FOUND;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_OK;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CancelReservationTest {

    @Autowired
    private ScheduleHandler scheduleHandler;

    @MockBean
    private ReservationRepository reservationRepository;
    @MockBean
    private ReservationMapper reservationMapper;
    @MockBean
    private AppointmentRepository appointmentRepository;
    @MockBean
    private JmsTemplate jmsTemplate;
    @MockBean
    private RestTemplate userServiceRestTemplate;
    @MockBean
    private TokenService tokenService;

    @Test
    public void cancelReservation_appointmentNotFound() {
        // Arrange
        Reservation r1 = ObjectMother.createReservation();

        Mockito.when(reservationMapper.reservationDtoToReservation(any(ReservationDto.class))).thenReturn(r1);
        Mockito.when(appointmentRepository.findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(Optional.empty());

        // Act
        Response<Boolean> result = scheduleHandler.cancelReservation("jwt", new ReservationDto());

        // Assert
        TestUtils.assertResponse(result, STATUS_NOT_FOUND, "Appointment not found!", false);
        Mockito.verify(appointmentRepository, Mockito.times(0)).save(any());
        Mockito.verify(reservationRepository, Mockito.times(0)).save(any());
        Mockito.verify(jmsTemplate, Mockito.times(0)).convertAndSend(anyString(), Optional.ofNullable(anyObject()));
    }

    @Test
    public void cancelReservation_noReservations() {
        // Arrange
        AppointmentUserDto userDto = ObjectMother.createAppointmentUserDto();
        Response<AppointmentUserDto> userResponse = ObjectMother.createUserResponse(userDto);

        Appointment a1 = ObjectMother.createClosedAppointment();
        Reservation r1 = ObjectMother.createReservation();

        Mockito.when(reservationMapper.reservationDtoToReservation(any(ReservationDto.class))).thenReturn(r1);
        Mockito.when(appointmentRepository.findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(Optional.of(a1));
        Mockito.when(tokenService.getRole(anyString())).thenReturn("USER");
        Mockito.when(reservationRepository.findAll()).thenReturn(List.of());
        Mockito.when(userServiceRestTemplate.exchange(
                Mockito.eq("user/getAppointmentUserData/" + userDto.getId()),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.<ParameterizedTypeReference<Response<AppointmentUserDto>>>any()
        )).thenReturn(new ResponseEntity<>(userResponse, HttpStatus.OK));

        // Act
        Response<Boolean> result = scheduleHandler.cancelReservation("jwt", new ReservationDto());

        // Assert
        TestUtils.assertResponse(result, STATUS_OK, "Reservation canceled", true);
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(any());
        Mockito.verify(reservationRepository, Mockito.times(0)).save(any());
        Mockito.verify(jmsTemplate, Mockito.times(0)).convertAndSend(anyString(), Optional.ofNullable(anyObject()));
    }

    @Test
    public void cancelReservation_managerSuccess() {
        // Arrange
        AppointmentUserDto userDto = ObjectMother.createAppointmentUserDto();
        Response<AppointmentUserDto> userResponse = ObjectMother.createUserResponse(userDto);

        Appointment a1 = ObjectMother.createClosedAppointment();
        Reservation r1 = new Reservation((LocalDate.now().plusDays(1)).toString(), 11, 12, "Sunday", 2L);
        Reservation r2 = new Reservation((LocalDate.now().plusDays(2)).toString(), 13, 14, "Friday", 1L);
        Reservation r3 = ObjectMother.createReservation();

        Mockito.when(reservationMapper.reservationDtoToReservation(any(ReservationDto.class))).thenReturn(r3);
        Mockito.when(appointmentRepository.findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(Optional.of(a1));
        Mockito.when(tokenService.getRole(anyString())).thenReturn("MANAGER");
        Mockito.when(reservationRepository.findAll()).thenReturn(List.of(r1, r2, r3));
        Mockito.when(userServiceRestTemplate.exchange(
                Mockito.eq("user/getAppointmentUserData/" + userDto.getId()),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.<ParameterizedTypeReference<Response<AppointmentUserDto>>>any()
        )).thenReturn(new ResponseEntity<>(userResponse, HttpStatus.OK));

        // Act
        Response<Boolean> result = scheduleHandler.cancelReservation("jwt", new ReservationDto());

        // Assert
        TestUtils.assertResponse(result, STATUS_OK, "Appointment closed", true);
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(any());
        Mockito.verify(reservationRepository, Mockito.times(1)).save(any());
        Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(anyString(), Optional.ofNullable(anyObject()));
    }

    @Test
    public void cancelReservation_userSuccess() {
        // Arrange
        AppointmentUserDto userDto = ObjectMother.createAppointmentUserDto();
        Response<AppointmentUserDto> userResponse = ObjectMother.createUserResponse(userDto);

        Appointment a1 = ObjectMother.createClosedAppointment();
        Reservation r1 = new Reservation((LocalDate.now().plusDays(1)).toString(), 11, 12, "Sunday", 2L);
        Reservation r2 = new Reservation((LocalDate.now().plusDays(2)).toString(), 13, 14, "Friday", 1L);
        Reservation r3 = ObjectMother.createReservation();

        Mockito.when(reservationMapper.reservationDtoToReservation(any(ReservationDto.class))).thenReturn(r3);
        Mockito.when(appointmentRepository.findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(anyString(), anyInt(), anyInt(), anyLong()))
                .thenReturn(Optional.of(a1));
        Mockito.when(tokenService.getRole(anyString())).thenReturn("USER");
        Mockito.when(reservationRepository.findAll()).thenReturn(List.of(r1, r2, r3));
        Mockito.when(userServiceRestTemplate.exchange(
                Mockito.eq("user/getAppointmentUserData/" + userDto.getId()),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.<ParameterizedTypeReference<Response<AppointmentUserDto>>>any()
        )).thenReturn(new ResponseEntity<>(userResponse, HttpStatus.OK));

        // Act
        Response<Boolean> result = scheduleHandler.cancelReservation("jwt", new ReservationDto());

        // Assert
        TestUtils.assertResponse(result, STATUS_OK, "Reservation canceled", true);
        Assertions.assertEquals(0, a1.getCurrentClients()); // Bocni efekat
        Assertions.assertTrue(a1.isOpen()); // Bocni efekat
        Mockito.verify(appointmentRepository, Mockito.times(1)).save(any());
        Mockito.verify(reservationRepository, Mockito.times(1)).save(any());
        Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(anyString(), Optional.ofNullable(anyObject()));
    }

}
