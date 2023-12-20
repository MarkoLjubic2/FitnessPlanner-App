package org.raf.sk.appointmentservice.mapper;

import lombok.NoArgsConstructor;
import org.raf.sk.appointmentservice.domain.Reservation;
import org.raf.sk.appointmentservice.dto.CreateReservationDto;
import org.raf.sk.appointmentservice.dto.ReservationDto;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ReservationMapper {

    public ReservationDto reservationToReservationDto(Reservation reservation) {
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setStartTime(reservation.getStartTime());
        reservationDto.setEndTime(reservation.getEndTime());
        reservationDto.setTrainingId(reservation.getTraining().getId());
        reservationDto.setClientId(reservation.getClientId());
        return reservationDto;
    }

    public Reservation reservationDtoToReservation(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        reservation.setId(reservationDto.getId());
        reservation.setStartTime(reservationDto.getStartTime());
        reservation.setEndTime(reservationDto.getEndTime());
        reservation.setClientId(reservationDto.getClientId());
        return reservation;
    }

    public CreateReservationDto reservationToCreateReservationDto(Reservation reservation) {
        CreateReservationDto createReservationDto = new CreateReservationDto();
        createReservationDto.setStartTime(reservation.getStartTime());
        createReservationDto.setEndTime(reservation.getEndTime());
        createReservationDto.setTrainingId(reservation.getTraining().getId());
        createReservationDto.setClientId(reservation.getClientId());
        return createReservationDto;
    }

    public Reservation createReservationDtoToReservation(CreateReservationDto createReservationDto) {
        Reservation reservation = new Reservation();
        reservation.setStartTime(createReservationDto.getStartTime());
        reservation.setEndTime(createReservationDto.getEndTime());
        reservation.setClientId(createReservationDto.getClientId());
        return reservation;
    }

}
