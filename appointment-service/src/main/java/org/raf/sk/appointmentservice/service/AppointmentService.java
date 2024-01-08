package org.raf.sk.appointmentservice.service;

import org.raf.sk.appointmentservice.domain.Schedulable;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.hall.CreateHallDto;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.raf.sk.appointmentservice.dto.hall.UpdateHallDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.dto.training.TrainingDto;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {

    Response<Page<HallDto>> findAllHalls(Pageable pageable);

    Response<HallDto> findHallById(Long hallId);

    Response<Boolean> createHall(String jwt, CreateHallDto createHallDto);

    Response<Boolean> updateHall(String jwt, UpdateHallDto updateHallDto);

    Response<Boolean> deleteHall(String jwt, Long hallId);

    Response<Boolean> scheduleReservation(AppointmentDto appointmentDto);

    Response<Boolean> cancelReservation(String jwt, ReservationDto reservationDto);

    Response<Page<AppointmentDto>> findAllAppointments(Pageable pageable);

    Response<Page<AppointmentDto>> findAppointmentByFilter(FilterCombinator<Schedulable> filter, Pageable pageable);

    Response<Page<ReservationDto>> findAllReservations(Pageable pageable);

    Response<Page<ReservationDto>> findReservationByFilter(FilterCombinator<Schedulable> filter, Pageable pageable);

    Response<TrainingDto> findTrainingById(Long trainingId);

    void reminder();

}
