package org.raf.sk.appointmentservice.dto.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {

    private Long reservationId;
    private String comment;
    private int mark;

}
