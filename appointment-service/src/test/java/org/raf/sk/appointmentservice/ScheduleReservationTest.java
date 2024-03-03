package org.raf.sk.appointmentservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.raf.sk.appointmentservice.client.user.AppointmentUserDto;
import org.raf.sk.appointmentservice.client.user.ManagerDto;
import org.raf.sk.appointmentservice.domain.Training;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.raf.sk.appointmentservice.repository.TrainingRepository;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_NOT_FOUND;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_OK;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ScheduleReservationTest {

    @Autowired
    private ScheduleHandler scheduleHandler;

    @MockBean
    private TrainingRepository trainingRepository;
    @MockBean
    private ReservationRepository reservationRepository;
    @MockBean
    private JmsTemplate jmsTemplate;
    @MockBean
    private RestTemplate userServiceRestTemplate;

    @Test
    public void scheduleReservation_trainingNull() {
        // Arrange
        AppointmentUserDto userDto = ObjectMother.createAppointmentUserDto();
        Response<AppointmentUserDto> response = ObjectMother.createUserResponse(userDto);

        AppointmentDto appointmentDto = ObjectMother.createAppointmentDto();

        Mockito.when(userServiceRestTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.<ParameterizedTypeReference<Response<AppointmentUserDto>>>any()
        )).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        Mockito.when(trainingRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Response<Boolean> result = scheduleHandler.scheduleReservation(appointmentDto);

        // Assert
        Assertions.assertEquals(STATUS_NOT_FOUND, result.getStatusCode());
        Assertions.assertEquals("Training not found", result.getMessage());
        Assertions.assertEquals(false, result.getData());
        Mockito.verify(trainingRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    public void scheduleReservation_scheduleSuccess() {
        // Arrange
        AppointmentUserDto userDto = ObjectMother.createAppointmentUserDto();
        Response<AppointmentUserDto> userResponse = ObjectMother.createUserResponse(userDto);

        ManagerDto managerDto = ObjectMother.createManagerDto();

        Response<ManagerDto> managerResponse = new Response<>();
        managerResponse.setStatusCode(200);
        managerResponse.setMessage("Success");
        managerResponse.setData(managerDto);

        AppointmentDto appointmentDto = ObjectMother.createAppointmentDto();

        Training t1 = ObjectMother.createTraining();

        Mockito.when(userServiceRestTemplate.exchange(
                Mockito.eq("user/getAppointmentUserData/" + userDto.getId()),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.<ParameterizedTypeReference<Response<AppointmentUserDto>>>any()
        )).thenReturn(new ResponseEntity<>(userResponse, HttpStatus.OK));
        Mockito.when(trainingRepository.findById(anyLong())).thenReturn(Optional.of(t1));
        Mockito.when(userServiceRestTemplate.exchange(
                Mockito.eq("user/getManagerData/" + managerDto.getId()),
                Mockito.eq(HttpMethod.GET),
                Mockito.isNull(),
                Mockito.<ParameterizedTypeReference<Response<ManagerDto>>>any()
        )).thenReturn(new ResponseEntity<>(managerResponse, HttpStatus.OK));

        // Act
        Response<Boolean> result = scheduleHandler.scheduleReservation(appointmentDto);

        // Assert
        Assertions.assertEquals(STATUS_OK, result.getStatusCode());
        Assertions.assertEquals("Reservation created", result.getMessage());
        Assertions.assertEquals(true, result.getData());
        Mockito.verify(jmsTemplate, Mockito.times(1)).convertAndSend(anyString(), Optional.ofNullable(anyObject()));
        Mockito.verify(reservationRepository, Mockito.times(1)).save(any());
    }

}
