package org.raf.sk.appointmentservice.dto.appointment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAppointmentDto {

    private String date;
    private Integer startTime;
    private Integer endTime;
    private Long trainingId;
    private Long clientId;
    private String day;
    private int maxClients;
    private int currentClients;

}
