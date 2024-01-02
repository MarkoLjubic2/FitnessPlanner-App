package org.raf.sk.appointmentservice.dto.hall;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateHallDto {

    private Long id;
    private String name;
    private String description;
    private int coaches;
    private Long managerId;

}
