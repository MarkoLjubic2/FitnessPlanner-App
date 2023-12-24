package org.raf.sk.appointmentservice.service.combinator;

import org.raf.sk.appointmentservice.domain.Reservation;

import java.util.function.Predicate;

public interface FilterCombinator extends Predicate<Reservation> {

    static FilterCombinator isType(String name) {
        return reservation -> reservation.getTraining().getName().equals(name);
    }

    static FilterCombinator isIndividual() {
        return reservation -> reservation.getTraining().isIndividual();
    }

    static FilterCombinator isGroup() {
        return reservation -> !reservation.getTraining().isIndividual();
    }

    default FilterCombinator and(FilterCombinator other) {
        return reservation -> this.test(reservation) && other.test(reservation);
    }

    default FilterCombinator or(FilterCombinator other) {
        return reservation -> this.test(reservation) || other.test(reservation);
    }

}
