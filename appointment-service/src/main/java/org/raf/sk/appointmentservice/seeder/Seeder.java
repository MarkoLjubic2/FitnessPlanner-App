package org.raf.sk.appointmentservice.seeder;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.domain.Review;
import org.raf.sk.appointmentservice.domain.Training;
import org.raf.sk.appointmentservice.repository.HallRepository;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.raf.sk.appointmentservice.repository.ReviewRepository;
import org.raf.sk.appointmentservice.repository.TrainingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Date;


@Profile({"default"})
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "seed.data", havingValue = "true")
public class Seeder implements CommandLineRunner {

    private final HallRepository hallRepository;
    private final TrainingRepository trainingRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void run(String... args) {
        insertTrainings();
        insertHalls();
        insertReservations();
        insertReviews();
    }

    private void insertTrainings() {
        Training training1 = new Training("Calisthenics", true, 1600);
        Training training2 = new Training("Pilates", false, 1000);
        Training training3 = new Training("Box", true, 2000);
        Training training4 = new Training("Jiu Jitsu", false, 1200);
        Training training5 = new Training("Taekwondo", false, 1300);
        Training training6 = new Training("MMA", false, 2300);
        Training training7 = new Training("Archery", false, 1900);
        Training training8 = new Training("Swimming", false, 1000);
        Training training9 = new Training("Ski Jumping", true, 3000);

        trainingRepository.save(training1);
        trainingRepository.save(training2);
        trainingRepository.save(training3);
        trainingRepository.save(training4);
        trainingRepository.save(training5);
        trainingRepository.save(training6);
        trainingRepository.save(training7);
        trainingRepository.save(training8);
        trainingRepository.save(training9);
    }

    private void insertHalls() {
        Hall hall1 = new Hall("Hall 1", "/", 2);
        trainingRepository.findTrainingByName("Calisthenics").ifPresent(hall1::setTraining);
        Hall hall2 = new Hall("Hall 2", "/", 1);
        trainingRepository.findTrainingByName("Pilates").ifPresent(hall2::setTraining);
        Hall hall3 = new Hall("Hall 3", "/", 2);
        trainingRepository.findTrainingByName("Box").ifPresent(hall3::setTraining);
        Hall hall4 = new Hall("Hall 4", "/", 1);
        trainingRepository.findTrainingByName("Jiu Jitsu").ifPresent(hall4::setTraining);

        hallRepository.save(hall1);
        hallRepository.save(hall2);
        hallRepository.save(hall3);
        hallRepository.save(hall4);
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


}
