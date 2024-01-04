package org.raf.sk.appointmentservice.dto.reservation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReservationDto {

    private String date;
    private Integer startTime;
    private Integer endTime;
    private Long trainingId;
    private Long clientId;
    private String day;

}
