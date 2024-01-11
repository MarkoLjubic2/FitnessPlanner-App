package org.raf.sk.appointmentservice.service.services;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.*;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.mapper.AppointmentMapper;
import org.raf.sk.appointmentservice.mapper.ReservationMapper;
import org.raf.sk.appointmentservice.repository.AppointmentRepository;
import org.raf.sk.appointmentservice.repository.HallRepository;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.raf.sk.appointmentservice.repository.TrainingRepository;
import org.raf.sk.appointmentservice.security.tokenService.TokenService;
import org.raf.sk.appointmentservice.service.Response;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.raf.sk.appointmentservice.constants.Constants.STATUS_NOT_FOUND;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_OK;

@Component
@AllArgsConstructor
public class SearchHandler {

    private final ReservationRepository reservationRepository;
    private final AppointmentRepository appointmentRepository;
    private final HallRepository hallRepository;
    private final TrainingRepository trainingRepository;
    private final ReservationMapper reservationMapper;
    private final AppointmentMapper appointmentMapper;
    private final TokenService tokenService;

    public Response<Page<AppointmentDto>> findAllAppointments(Pageable pageable) {
        return new Response<>(STATUS_OK, "All appointments", appointmentRepository.findAll(pageable).map(appointmentMapper::appointmentToAppointmentDto));
    }

    public Response<Page<AppointmentDto>> findAppointmentByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findAll(pageable);
        List<Appointment> filteredAppointments = page.getContent().stream()
                .filter(filter)
                .collect(Collectors.toList());

        Page<Appointment> filteredPage = new PageImpl<>(filteredAppointments, pageable, filteredAppointments.size());
        if (filteredAppointments.isEmpty())
            return new Response<>(STATUS_NOT_FOUND, "Appointments not found", null);

        return new Response<>(STATUS_OK, "Appointments found", filteredPage.map(appointmentMapper::appointmentToAppointmentDto));
    }

    public Response<Page<ReservationDto>> findAllReservations(Pageable pageable) {
        return new Response<>(STATUS_OK, "All reservations", reservationRepository.findAll(pageable).map(reservationMapper::reservationToReservationDto));
    }

    public Response<Page<ReservationDto>> findReservationByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        Page<Reservation> page = reservationRepository.findAll(pageable);
        List<Reservation> filteredReservations = page.getContent().stream()
                .filter(filter)
                .collect(Collectors.toList());

        Page<Reservation> filteredPage = new PageImpl<>(filteredReservations, pageable, filteredReservations.size());
        if (filteredReservations.isEmpty())
            return new Response<>(STATUS_NOT_FOUND, "Reservations not found", null);

        return new Response<>(STATUS_OK, "Reservations found", filteredPage.map(reservationMapper::reservationToReservationDto));
    }

    public Response<Page<ReservationDto>> getReservationByManager(String jwt) {
        Long managerId = tokenService.getUserId(jwt);
        Optional<Hall> hall = hallRepository.findHallByManagerId(managerId);
        List<Training> training = trainingRepository.findAll();

        List<Training> trainings = training.stream().filter(t -> t.getHall().getId().equals(hall.get().getId())).toList();
        List<Reservation> reservations = reservationRepository.findAll();
        List<Reservation> filteredReservations = reservations.stream().filter(r -> trainings.contains(r.getTraining())).toList();

        return new Response<>(STATUS_OK, "Reservations found", new PageImpl<>(filteredReservations, Pageable.unpaged(), filteredReservations.size()).map(reservationMapper::reservationToReservationDto));
    }

}
