package org.raf.sk.notificationservice.service.factory;

import org.raf.sk.notificationservice.domain.Type;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;

@FunctionalInterface
public interface MessageBuilder<T extends NotificationDto> {

    String build(Type type, T dto);

}
