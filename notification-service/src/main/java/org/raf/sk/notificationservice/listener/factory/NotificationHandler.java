package org.raf.sk.notificationservice.listener.factory;

import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;

@FunctionalInterface
public interface NotificationHandler<T extends NotificationDto> {

    void handle(T notificationDto);

}
