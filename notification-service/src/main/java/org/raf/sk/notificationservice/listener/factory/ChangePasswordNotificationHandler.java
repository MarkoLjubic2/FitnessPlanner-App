package org.raf.sk.notificationservice.listener.factory;

import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.dto.ChangePasswordDto;
import org.raf.sk.notificationservice.service.NotificationService;

@AllArgsConstructor
public class ChangePasswordNotificationHandler implements NotificationHandler<ChangePasswordDto> {

    private final NotificationService notificationService;

    @Override
    public void handle(ChangePasswordDto notificationDto) {
        notificationService.sendNotification("CHANGE_PASSWORD", notificationDto);
    }
}
