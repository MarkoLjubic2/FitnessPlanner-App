package org.raf.sk.appointmentservice.service;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.*;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.hall.CreateHallDto;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.raf.sk.appointmentservice.dto.hall.UpdateHallDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.dto.training.TrainingDto;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;
import org.raf.sk.appointmentservice.service.services.HallHandler;
import org.raf.sk.appointmentservice.service.services.ScheduleHandler;
import org.raf.sk.appointmentservice.service.services.SearchHandler;
import org.raf.sk.appointmentservice.service.services.TrainingHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final HallHandler hallHandler;
    private final ScheduleHandler scheduleHandler;
    private final SearchHandler searchHandler;
    private final TrainingHandler trainingHandler;

    // Hall Handler

    @Override
    public Response<Page<HallDto>> findAllHalls(Pageable pageable) {
        return hallHandler.findAllHalls(pageable);
    }

    @Override
    public Response<HallDto> findHallById(Long hallId) {
        return hallHandler.findHallById(hallId);
    }

    @Override
    public Response<Boolean> createHall(String jwt, CreateHallDto createHallDto) {
        return hallHandler.createHall(jwt, createHallDto);
    }

    @Override
    public Response<Boolean> updateHall(String jwt, UpdateHallDto updateHallDto) {
        return hallHandler.updateHall(jwt, updateHallDto);
    }

    @Override
    public Response<Boolean> deleteHall(String jwt, Long hallId) {
        return hallHandler.deleteHall(jwt, hallId);
    }

    // Schedule Handler

    @Override
    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Response<Boolean> scheduleReservation(AppointmentDto appointmentDto) {
        return scheduleHandler.scheduleReservation(appointmentDto);
    }

    @Override
    @Retryable(value = {HttpServerErrorException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public Response<Boolean> cancelReservation(String jwt, ReservationDto reservationDto) {
        return scheduleHandler.cancelReservation(jwt, reservationDto);
    }

    @Override
    public Response<Page<AppointmentDto>> findAllAppointments(Pageable pageable) {
        return searchHandler.findAllAppointments(pageable);
    }

    @Override
    public Response<Page<AppointmentDto>> findAppointmentByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        return searchHandler.findAppointmentByFilter(filter, pageable);
    }

    @Override
    public Response<Page<ReservationDto>> findAllReservations(Pageable pageable) {
        return searchHandler.findAllReservations(pageable);
    }

    @Override
    public Response<Page<ReservationDto>> findReservationByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        return searchHandler.findReservationByFilter(filter, pageable);
    }

    // Training Handler

    @Override
    public Response<TrainingDto> findTrainingById(Long trainingId) {
        return trainingHandler.findTrainingById(trainingId);
    }

    @Override
    public Response<Page<ReservationDto>> getReservationByManager(String jwt) {
        return searchHandler.getReservationByManager(jwt);
    }

    @Override
    public void reminder() {
        scheduleHandler.reminder();
    }

    @Override
    public void cancelTrainingsWithLessThanThreeClients() {
        scheduleHandler.cancelTrainingsWithLessThanThreeClients();
    }


}
