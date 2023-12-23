package org.raf.sk.appointmentservice.dto.hall;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HallDto {

    private Long id;
    private String name;
    private String description;
    private int coaches;

}
