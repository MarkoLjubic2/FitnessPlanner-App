package org.raf.sk.notificationservice.listener.factory;

import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.raf.sk.notificationservice.service.NotificationService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationHandlerFactory {

    private final Map<String, NotificationHandler<? extends NotificationDto>> handlers;

    public NotificationHandlerFactory(NotificationService notificationService) {
        handlers = new HashMap<>();
        handlers.put("ACTIVATION", new ActivationNotificationHandler(notificationService));
        handlers.put("CHANGE_PASSWORD", new ChangePasswordNotificationHandler(notificationService));
        handlers.put("RESERVATION", new ReservationNotificationHandler(notificationService));
        handlers.put("RESERVATION_CANCEL", new CancelReservationNotificationHandler(notificationService));
    }

    public <T extends NotificationDto> NotificationHandler<T> getHandler(String type) {
        NotificationHandler<T> handler = (NotificationHandler<T>) handlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("Unknown message type: " + type);
        }
        return handler;
    }
}