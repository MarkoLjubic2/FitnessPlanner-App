package org.raf.sk.appointmentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHallDto {

    private String name;
    private String description;
    private int coaches;

}
