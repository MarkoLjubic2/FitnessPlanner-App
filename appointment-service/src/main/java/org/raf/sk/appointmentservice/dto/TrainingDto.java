package org.raf.sk.appointmentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingDto {

    private Long id;
    private String name;
    private boolean individual;
    private double price;
}
