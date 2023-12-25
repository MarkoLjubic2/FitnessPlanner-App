package org.raf.sk.appointmentservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.raf.sk.appointmentservice.domain.Schedulable;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;
import org.raf.sk.appointmentservice.service.combinator.FilterJSON;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class JSONController {

    public FilterCombinator<Schedulable> convertFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        FilterJSON filterJSON;
        try {
            filterJSON = objectMapper.readValue(json, FilterJSON.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        FilterCombinator<Schedulable> filterCombinator = FilterCombinator.initialize();

        if (filterJSON.getType() != null) {
            filterCombinator = filterCombinator.and(FilterCombinator.isType(filterJSON.getType()));
        }

        if (filterJSON.getDay() != null) {
            filterCombinator = filterCombinator.and(FilterCombinator.isDay(DayOfWeek.valueOf(filterJSON.getDay().name())));
        }

        if (filterJSON.getIndividual() != null) {
            filterCombinator = filterCombinator.and(filterJSON.getIndividual() ?
                    FilterCombinator.isIndividualTraining() : FilterCombinator.isGroupTraining());
        }


        return filterCombinator;
    }


}
