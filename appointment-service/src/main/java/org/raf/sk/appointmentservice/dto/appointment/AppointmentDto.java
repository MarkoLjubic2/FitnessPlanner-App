package org.raf.sk.appointmentservice.dto.appointment;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.Date;

@Getter
@Setter
public class AppointmentDto {

    private Long id;
    private Date date;
    private Integer startTime;
    private Integer endTime;
    private Long trainingId;
    private Long clientId;
    private DayOfWeek dayOfWeek;
    private int maxClients;
    private int currentClients;

}
