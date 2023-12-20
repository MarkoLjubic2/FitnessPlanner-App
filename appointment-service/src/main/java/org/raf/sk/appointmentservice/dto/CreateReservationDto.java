package org.raf.sk.appointmentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReservationDto {

    private Integer startTime;
    private Integer endTime;
    private Long trainingId;
    private Long clientId;

}
