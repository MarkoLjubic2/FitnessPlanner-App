package org.raf.sk.appointmentservice.mapper;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.reservation.CreateReservationDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.repository.TrainingRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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
                    reservationDto.setDay(r.getDay());
                    reservationDto.setCanceled(r.isCanceled());
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
                    reservation.setDay(dto.getDay());
                    reservation.setTraining(trainingRepository.findById(dto.getTrainingId()).orElse(null));
                    reservation.setCanceled(dto.isCanceled());
                    return reservation;
                })
                .orElse(null);
    }

    public Reservation createReservationDtoToReservation(CreateReservationDto createReservationDto) {
        return Optional.ofNullable(createReservationDto)
                .map(dto -> {
                    Reservation reservation = new Reservation();
                    reservation.setDate(LocalDate.now().toString());
                    reservation.setStartTime(dto.getStartTime());
                    reservation.setEndTime(dto.getEndTime());
                    reservation.setClientId(dto.getClientId());
                    trainingRepository.findById(dto.getTrainingId()).ifPresent(reservation::setTraining);
                    reservation.setDay(dto.getDay());
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
                    createReservationDto.setDay(dto.getDay());
                    return createReservationDto;
                })
                .orElse(null);
    }

}
