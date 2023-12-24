package org.raf.sk.appointmentservice.seeder;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.*;
import org.raf.sk.appointmentservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.DayOfWeek;


@Profile({"default"})
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "seed.data", havingValue = "true")
public class Seeder implements CommandLineRunner {

    private final HallRepository hallRepository;
    private final TrainingRepository trainingRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public void run(String... args) {
        insertHalls();
        insertTrainings();
        insertReservations();
        insertReviews();
        insertAppointments();
    }

    private void insertHalls() {
        Hall hall1 = new Hall("Hall 1", "/", 2);
        Hall hall2 = new Hall("Hall 2", "/", 1);
        Hall hall3 = new Hall("Hall 3", "/", 2);
        Hall hall4 = new Hall("Hall 4", "/", 1);

        hallRepository.save(hall1);
        hallRepository.save(hall2);
        hallRepository.save(hall3);
        hallRepository.save(hall4);
    }

    private void insertTrainings() {
        Training training1 = new Training("Calisthenics", true, 1600);
        hallRepository.findHallByName("Hall 1").ifPresent(training1::setHall);
        Training training2 = new Training("Pilates", false, 1000);
        hallRepository.findHallByName("Hall 2").ifPresent(training2::setHall);
        Training training3 = new Training("Box", true, 2000);
        hallRepository.findHallByName("Hall 3").ifPresent(training3::setHall);

        trainingRepository.save(training1);
        trainingRepository.save(training2);
        trainingRepository.save(training3);
    }

    private void insertReservations() {
        Reservation reservation1 = new Reservation(new Date(2021, 1, 1), 12, 14, 1L);
        reservation1.setTraining(trainingRepository.findTrainingByName("Calisthenics").orElseThrow());
        Reservation reservation2 = new Reservation(new Date(2021, 1, 3), 14, 16, 1L);
        reservation2.setTraining(trainingRepository.findTrainingByName("Box").orElseThrow());

        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
    }

    private void insertReviews() {
        Review review1 = new Review("Great", 5);
        reservationRepository.findById(1L).ifPresent(review1::setReservation);
        Review review2 = new Review("Bad", 1);
        reservationRepository.findById(2L).ifPresent(review2::setReservation);

        reviewRepository.save(review1);
        reviewRepository.save(review2);
    }

    private void insertAppointments() {
        Appointment appointment1 = new Appointment(new Date(2021, 1, 1), 12, 14, 10, 0, DayOfWeek.MONDAY);
        appointment1.setTraining(trainingRepository.findTrainingByName("Calisthenics").orElseThrow());
        Appointment appointment2 = new Appointment(new Date(2021, 1, 3), 14, 16,10, 0, DayOfWeek.MONDAY);
        appointment2.setTraining(trainingRepository.findTrainingByName("Box").orElseThrow());

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);
    }


}
