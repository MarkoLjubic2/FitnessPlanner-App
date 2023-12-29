package org.raf.sk.appointmentservice.mapper;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.reservation.CreateReservationDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.repository.TrainingRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ReservationMapper {

    private final TrainingRepository trainingRepository;

    public ReservationDto reservationToReservationDto(Reservation reservation) {
        return Optional.ofNullable(reservation)
                .map(r -> {
                    ReservationDto reservationDto = new ReservationDto();
                    reservationDto.setId(r.getId());
                    reservationDto.setDate(r.getDate());
                    reservationDto.setStartTime(r.getStartTime());
                    reservationDto.setEndTime(r.getEndTime());
                    reservationDto.setTrainingId(r.getTraining().getId());
                    reservationDto.setClientId(r.getClientId());
                    return reservationDto;
                })
                .orElse(null);
    }

    public Reservation reservationDtoToReservation(ReservationDto reservationDto) {
        return Optional.ofNullable(reservationDto)
                .map(dto -> {
                    Reservation reservation = new Reservation();
                    reservation.setId(dto.getId());
                    reservation.setDate(dto.getDate());
                    reservation.setStartTime(dto.getStartTime());
                    reservation.setEndTime(dto.getEndTime());
                    reservation.setClientId(dto.getClientId());
                    return reservation;
                })
                .orElse(null);
    }

    public CreateReservationDto reservationToCreateReservationDto(Reservation reservation) {
        return Optional.ofNullable(reservation)
                .map(r -> {
                    CreateReservationDto createReservationDto = new CreateReservationDto();
                    createReservationDto.setDate(r.getDate());
                    createReservationDto.setStartTime(r.getStartTime());
                    createReservationDto.setEndTime(r.getEndTime());
                    createReservationDto.setTrainingId(r.getTraining().getId());
                    createReservationDto.setClientId(r.getClientId());
                    return createReservationDto;
                })
                .orElse(null);
    }

    public Reservation createReservationDtoToReservation(CreateReservationDto createReservationDto) {
        return Optional.ofNullable(createReservationDto)
                .map(dto -> {
                    Reservation reservation = new Reservation();
                    reservation.setDate(new Date());
                    reservation.setStartTime(dto.getStartTime());
                    reservation.setEndTime(dto.getEndTime());
                    reservation.setClientId(dto.getClientId());
                    trainingRepository.findById(dto.getTrainingId()).ifPresent(reservation::setTraining);
                    return reservation;
                })
                .orElse(null);
    }

    public CreateReservationDto appointmentDtoToCreateReservationDto(AppointmentDto appointmentDto) {
        return Optional.ofNullable(appointmentDto)
                .map(dto -> {
                    CreateReservationDto createReservationDto = new CreateReservationDto();
                    createReservationDto.setClientId(dto.getClientId());
                    createReservationDto.setDate(dto.getDate());
                    createReservationDto.setStartTime(dto.getStartTime());
                    createReservationDto.setEndTime(dto.getEndTime());
                    createReservationDto.setTrainingId(dto.getTrainingId());
                    return createReservationDto;
                })
                .orElse(null);
    }

}
