package org.raf.sk.appointmentservice.service.combinator;

import org.raf.sk.appointmentservice.domain.Schedulable;

import java.time.DayOfWeek;
import java.util.function.Predicate;

public interface FilterCombinator<T extends Schedulable> extends Predicate<T> {

    static <T extends Schedulable> FilterCombinator<T> isDay(DayOfWeek day) {
        return reservation -> reservation.getDayOfWeek().equals(day);
    }

    static <T extends Schedulable> FilterCombinator<T> isIndividual() {
        return reservation -> reservation.getTraining().isIndividual();
    }

    static <T extends Schedulable> FilterCombinator<T> isGroup() {
        return reservation -> !reservation.getTraining().isIndividual();
    }

    static <T extends Schedulable> FilterCombinator<T> isType(String type) {
        return reservation -> reservation.getTraining().getName().equals(type);
    }

    default FilterCombinator<T> and(FilterCombinator<T> other) {
        return reservation -> this.test(reservation) && other.test(reservation);
    }

    default FilterCombinator<T> or(FilterCombinator<T> other) {
        return reservation -> this.test(reservation) || other.test(reservation);
    }


}
