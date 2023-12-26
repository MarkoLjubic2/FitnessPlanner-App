package org.raf.sk.appointmentservice.service;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Appointment;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.domain.Schedulable;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.hall.CreateHallDto;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.raf.sk.appointmentservice.dto.hall.UpdateHallDto;
import org.raf.sk.appointmentservice.dto.reservation.CreateReservationDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.listener.MessageHelper;
import org.raf.sk.appointmentservice.mapper.AppointmentMapper;
import org.raf.sk.appointmentservice.mapper.HallMapper;
import org.raf.sk.appointmentservice.mapper.ReservationMapper;
import org.raf.sk.appointmentservice.repository.AppointmentRepository;
import org.raf.sk.appointmentservice.repository.HallRepository;
import org.raf.sk.appointmentservice.repository.ReservationRepository;
import org.raf.sk.appointmentservice.security.tokenService.TokenService;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final HallRepository hallRepository;
    private final ReservationRepository reservationRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final HallMapper hallMapper;
    private final ReservationMapper reservationMapper;
    private final AppointmentMapper appointmentMapper;
    private final JmsTemplate jmsTemplate;
    private final MessageHelper messageHelper;
    private final TokenService tokenService;

    @Override
    public Response<Page<HallDto>> findAllHalls(Pageable pageable) {
        return new Response<>(200, "All halls", hallRepository.findAll(pageable).map(hallMapper::hallToHallDto));
    }

    @Override
    public Response<HallDto> findHallById(Long hallId) {
        return hallRepository.getHallById(hallId)
                .map(hallMapper::hallToHallDto)
                .map(hallDto -> new Response<>(200, "Hall found", hallDto))
                .orElseGet(() -> new Response<>(404, "Hall not found", null));
    }

    @Override
    public Response<Boolean> createHall(String jwt, CreateHallDto createHallDto) {
        return null;
    }

    @Override
    public Response<Boolean> updateHall(String jwt, UpdateHallDto updateHallDto) {
        return null;
    }

    @Override
    public Response<Boolean> deleteHall(String jwt, Long hallId) {
        return null;
    }

    @Override
    public Response<Boolean> scheduleReservation(String jwt, AppointmentDto appointmentDto) {

        CreateReservationDto createReservationDto = reservationMapper.appointmentDtoToCreateReservationDto(appointmentDto);

        Reservation reservation = reservationMapper.createReservationDtoToReservation(createReservationDto);

        reservationRepository.save(reservation);

        Appointment appointment = appointmentMapper.appointmentDtoToAppointment(appointmentDto);

        if (appointment != null) {
            appointment.setCurrentClients(appointment.getCurrentClients() + 1);

            if (appointment.getMaxClients() == appointment.getCurrentClients()) {
                appointment.setOpen(false);
            }

            appointmentRepository.save(appointment);
        }

        return new Response<>(200, "Reservation created", true);
    }

    @Override
    public Response<Boolean> cancelReservation(ReservationDto reservationDto) {

        Reservation reservation = reservationMapper.reservationDtoToReservation(reservationDto);

        reservationRepository.delete(reservation);

        Appointment appointment = appointmentRepository.findAppointmentByDateAndStartTimeAndEndTimeAndTrainingId(String.valueOf(reservation.getDate()), reservation.getStartTime(), reservation.getEndTime(), reservation.getTraining().getId()).orElse(null);

        if (appointment != null) {
            appointment.setCurrentClients(appointment.getCurrentClients() - 1);
            appointment.setOpen(true);
            appointmentRepository.save(appointment);
        }

        return new Response<>(200, "Reservation canceled", true);
    }

    @Override
    public Response<Page<AppointmentDto>> findAllAppointments(Pageable pageable) {
        return null;
    }

    @Override
    public Response<Page<AppointmentDto>> findAppointmentByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        return null;
    }

    @Override
    public Response<Page<ReservationDto>> findAllReservations(Pageable pageable) {
        return null;
    }

    @Override
    public Response<Page<ReservationDto>> findReservationByFilter(FilterCombinator<Schedulable> filter, Pageable pageable) {
        Page<Reservation> page = reservationRepository.findAll(pageable);
        List<Reservation> filteredReservations = page.getContent().stream()
                .filter(filter)
                .collect(Collectors.toList());

        Page<Reservation> filteredPage = new PageImpl<>(filteredReservations, pageable, filteredReservations.size());

        if (filteredReservations.isEmpty())
            return new Response<>(404, "Reservation not found", null);

        return new Response<>(200, "Reservation found", filteredPage.map(reservationMapper::reservationToReservationDto));
    }


}
