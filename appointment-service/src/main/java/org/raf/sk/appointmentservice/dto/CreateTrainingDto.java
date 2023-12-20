package org.raf.sk.appointmentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTrainingDto {

    private String name;
    private boolean individual;
    private double price;

}
