package org.raf.sk.notificationservice.listener.factory;

import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.dto.PasswordChangeDto;
import org.raf.sk.notificationservice.service.NotificationService;

@AllArgsConstructor
public class ChangePasswordNotificationHandler implements NotificationHandler<PasswordChangeDto> {

    private final NotificationService notificationService;

    @Override
    public void handle(PasswordChangeDto notificationDto) {
        notificationService.sendNotification("CHANGE_PASSWORD", notificationDto);
    }
}
