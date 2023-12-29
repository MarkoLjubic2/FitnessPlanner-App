package org.raf.sk.notificationservice.listener.factory;

import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.dto.ActivationDto;
import org.raf.sk.notificationservice.service.NotificationService;

@AllArgsConstructor
public class ActivationNotificationHandler implements NotificationHandler<ActivationDto> {

    private final NotificationService notificationService;

    @Override
    public void handle(ActivationDto notificationDto) {
        notificationService.sendNotification("ACTIVATION", notificationDto);
    }
}
