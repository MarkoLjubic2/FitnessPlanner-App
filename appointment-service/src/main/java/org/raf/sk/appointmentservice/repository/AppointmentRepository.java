package org.raf.sk.appointmentservice.repository;

import org.raf.sk.appointmentservice.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> getAppointmentById(Long id);

    Optional<Appointment> findAppointmentByStartTime(Integer startTime);

    Optional<Appointment> findAppointmentByEndTime(Integer endTime);

    Optional<Appointment> findAppointmentByTrainingId(Long trainingId);

}
