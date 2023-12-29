package org.raf.sk.appointmentservice.repository;

import org.raf.sk.appointmentservice.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> getAppointmentById(Long id);

    Optional<Appointment> findAppointmentByStartTime(Integer startTime);

    Optional<Appointment> findAppointmentByEndTime(Integer endTime);

    Optional<Appointment> findAppointmentByTrainingId(Long trainingId);

    @Query("select a from Appointment a where a.date = :date and a.startTime = :startTime and a.endTime = :endTime and a.training.id = :trainingId")
    Optional<Appointment> findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(Date date, Integer startTime, Integer endTime, Long trainingId);

}
