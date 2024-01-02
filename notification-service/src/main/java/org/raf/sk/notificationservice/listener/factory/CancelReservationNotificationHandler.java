package org.raf.sk.notificationservice.listener.factory;

import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.dto.AppointmentReservationDto;
import org.raf.sk.notificationservice.service.NotificationService;

@AllArgsConstructor
public class CancelReservationNotificationHandler implements NotificationHandler<AppointmentReservationDto> {

    private final NotificationService notificationService;

    @Override
    public void handle(AppointmentReservationDto notificationDto) {
        notificationService.sendNotification("RESERVATION_CANCEL", notificationDto);
    }
}
