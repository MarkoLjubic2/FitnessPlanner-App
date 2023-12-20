package org.raf.sk.appointmentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDto {

    private Long id;
    private Integer startTime;
    private Integer endTime;
    private Long trainingId;
    private Long clientId;

}
