package org.raf.sk.appointmentservice.seeder;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.*;
import org.raf.sk.appointmentservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
        Hall hall1 = new Hall("Ahiley 1", "/", 2, 1L);
        Hall hall2 = new Hall("RAF Fitness", "/", 1, 2L);
        Hall hall3 = new Hall("TryCatch Gym", "/", 2, 1L);
        Hall hall4 = new Hall("Lambda Gym 2", "/", 1, 1L);

        hallRepository.save(hall1);
        hallRepository.save(hall2);
        hallRepository.save(hall3);
        hallRepository.save(hall4);
    }

    private void insertTrainings() {
        Training training1 = new Training("Calisthenics", true, 1600);
        hallRepository.findHallByName("Ahiley 1").ifPresent(training1::setHall);
        Training training2 = new Training("Pilates", false, 1000);
        hallRepository.findHallByName("RAF Fitness").ifPresent(training2::setHall);
        Training training3 = new Training("Box", true, 2000);
        hallRepository.findHallByName("TryCatch Gym").ifPresent(training3::setHall);

        trainingRepository.save(training1);
        trainingRepository.save(training2);
        trainingRepository.save(training3);
    }

    private void insertReservations() {
        Reservation reservation1 = new Reservation("2022-12-09", 12, 14, "MONDAY", 3L);
        reservation1.setTraining(trainingRepository.findTrainingByName("Calisthenics").orElseThrow());
        Reservation reservation2 = new Reservation("2022-12-19", 14, 16, "WEDNESDAY", 3L);
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
        Appointment appointment1 = new Appointment("2022-12-09", 12, 14, 1, 0, "MONDAY");
        appointment1.setTraining(trainingRepository.findTrainingByName("Calisthenics").orElseThrow());
        Appointment appointment2 = new Appointment("2022-12-19", 14, 16,1, 0, "WEDNESDAY");
        appointment2.setTraining(trainingRepository.findTrainingByName("Box").orElseThrow());

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);
    }


}
