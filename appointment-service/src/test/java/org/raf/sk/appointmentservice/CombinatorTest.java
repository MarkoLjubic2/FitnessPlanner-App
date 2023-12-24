package org.raf.sk.appointmentservice;

import org.junit.jupiter.api.Test;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.domain.Schedulable;
import org.raf.sk.appointmentservice.domain.Training;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CombinatorTest {

    @Test
    public void single_filter_test() {
        // Arrange
        Hall hall1 = new Hall("Hall 1", "/", 2);
        Hall hall2 = new Hall("Hall 2", "/", 1);

        Training training1 = new Training("Calisthenics", true, 1600);
        training1.setHall(hall1);
        Training training2 = new Training("Pilates", false, 1000);
        training2.setHall(hall2);

        Reservation reservation1 = new Reservation(new Date(2021, 1, 1), 12, 14, DayOfWeek.MONDAY, 1L);
        reservation1.setTraining(training1);
        Reservation reservation2 = new Reservation(new Date(2021, 1, 3), 14, 16, DayOfWeek.MONDAY,1L);
        reservation2.setTraining(training2);

        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);
        FilterCombinator<Reservation> filter = FilterCombinator.isType("Calisthenics");

        // Act
        long count = reservations.stream()
                .filter(filter)
                .count();

        // Assert
        assertEquals(1, count);
    }

    @Test
    public void multiple_filter_test() {
        // Arrange
        Hall hall1 = new Hall("Hall 1", "/", 2);
        Hall hall2 = new Hall("Hall 2", "/", 1);

        Training training1 = new Training("Calisthenics", true, 1600);
        training1.setHall(hall1);
        Training training2 = new Training("Pilates", false, 1000);
        training2.setHall(hall2);

        Reservation reservation1 = new Reservation(new Date(2021, 1, 1), 12, 14, DayOfWeek.MONDAY, 1L);
        reservation1.setTraining(training1);
        Reservation reservation2 = new Reservation(new Date(2021, 1, 3), 14, 16, DayOfWeek.MONDAY, 1L);
        reservation2.setTraining(training2);

        List<Reservation> reservations = Arrays.asList(reservation1, reservation2);
        FilterCombinator<Schedulable> filter = FilterCombinator.isIndividualTraining().and(FilterCombinator.isType("Calisthenics"));

        // Act
        long count = reservations.stream()
                .filter(filter)
                .count();

        // Assert
        assertEquals(1, count);
    }

}
