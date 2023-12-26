package org.raf.sk.appointmentservice.service.combinator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterJSON {

    @JsonProperty("day")
    private DayOfWeek day;
    @JsonProperty("individual")
    private Boolean individual;
    @JsonProperty("type")
    private String type;

}
