package org.raf.sk.notificationservice.mapper;


import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.domain.Notification;
import org.raf.sk.notificationservice.dto.ActivationDto;
import org.raf.sk.notificationservice.dto.ChangePasswordDto;
import org.raf.sk.notificationservice.dto.ReminderDto;
import org.raf.sk.notificationservice.dto.ReservationDto;
import org.raf.sk.notificationservice.repository.NotificationRepository;
import org.raf.sk.notificationservice.repository.TypeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class NotificationMapper {

    private final NotificationRepository notificationRepository;
    private final TypeRepository typeRepository;

    /*
    public ActivationDto notificationToActivationDto(Notification notification) {
        return Optional.ofNullable(notification)
                .map(n -> {
                    ActivationDto activationDto = new ActivationDto();
                    activationDto.setClientEmail(n.getClientEmail());
                    activationDto.setClientFirstName(n.getClientFirstName());
                    activationDto.setClientLastName(n.getClientLastName());
                    return activationDto;
                })
                .orElse(null);
    }

    public ChangePasswordDto notificationToChangePasswordDto(Notification notification) {
        return Optional.ofNullable(notification)
                .map(n -> {
                    ChangePasswordDto changePasswordDto = new ChangePasswordDto();
                    changePasswordDto.setClientEmail(n.getClientEmail());
                    return changePasswordDto;
                })
                .orElse(null);
    }

    public ReservationDto notificationToReservationDto(Notification notification) {
        return Optional.ofNullable(notification)
                .map(n -> {
                    ReservationDto reservationDto = new ReservationDto();
                    reservationDto.setClientEmail(n.getClientEmail());
                    reservationDto.setManagerEmail(n.getManagerEmail());
                    reservationDto.setClientFirstName(n.getClientFirstName());
                    reservationDto.setClientLastName(n.getClientLastName());
                    reservationDto.setHallName(n.getHallName());
                    return reservationDto;
                })
                .orElse(null);
    }

    public ReminderDto notificationToReminderDto(Notification notification) {
        return Optional.ofNullable(notification)
                .map(n -> {
                    ReminderDto reminderDto = new ReminderDto();
                    reminderDto.setClientEmail(n.getClientEmail());
                    reminderDto.setClientFirstName(n.getClientFirstName());
                    reminderDto.setClientLastName(n.getClientLastName());
                    reminderDto.setHallName(n.getHallName());
                    return reminderDto;
                })
                .orElse(null);
    }
    */

}
