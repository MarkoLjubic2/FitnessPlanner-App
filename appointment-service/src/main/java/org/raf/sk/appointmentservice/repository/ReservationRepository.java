package org.raf.sk.appointmentservice.repository;

import org.raf.sk.appointmentservice.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> getReservationById(Long id);

    Optional<Reservation> findReservationByStartTime(Integer startTime);

    Optional<Reservation> findReservationByEndTime(Integer endTime);

    Optional<Reservation> findReservationByTrainingId(Long trainingId);

    Optional<Reservation> findReservationByClientId(Long clientId);

    @Modifying
    @Query("update Reservation r set r.startTime = :startTime, r.endTime = :endTime, r.training = :trainingId, r.clientId = :clientId where r.id = :id")
    void updateReservationById(Long id, Integer startTime, Integer endTime, Long trainingId, Long clientId);

    @Modifying
    @Query("delete from Reservation r where r.id = :id")
    void deleteReservationById(Long id);




}
