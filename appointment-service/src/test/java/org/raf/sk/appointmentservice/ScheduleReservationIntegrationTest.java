package org.raf.sk.appointmentservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.service.Response;
import org.raf.sk.appointmentservice.service.services.ScheduleHandler;
import org.raf.sk.appointmentservice.util.ObjectMother;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_OK;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ScheduleReservationIntegrationTest {

    @Autowired
    private ScheduleHandler scheduleHandler;
    @MockBean
    private JmsTemplate jmsTemplate; // Ne treba da se mokuje, ali zbog slanja mejlova mokujem

    @Test
    public void scheduleReservation_success() {
        AppointmentDto appointmentDto = ObjectMother.createAppointmentDto();
        Mockito.doNothing().when(jmsTemplate).convertAndSend(anyString(), anyString());

        Response<Boolean> result = scheduleHandler.scheduleReservation(appointmentDto);

        Assertions.assertEquals("Reservation created", result.getMessage());
        Assertions.assertEquals(STATUS_OK, result.getStatusCode());
        Assertions.assertTrue(result.getData());
    }

}
