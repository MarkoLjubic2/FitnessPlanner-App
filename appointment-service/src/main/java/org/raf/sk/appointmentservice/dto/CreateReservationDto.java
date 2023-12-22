package org.raf.sk.appointmentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CreateReservationDto {

    private Date date;
    private Integer startTime;
    private Integer endTime;
    private Long trainingId;
    private Long clientId;

}
