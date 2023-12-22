package org.raf.sk.notificationservice.service;

import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    <T> Response<Boolean> sendNotification(T dto);

    Response<Boolean> deleteNotification(Long id);

    <T extends NotificationDto> Response<Page<T>> findAll(Pageable pageable);

    Response<? extends NotificationDto> getNotificationById(Long id);

}
