package org.raf.sk.notificationservice.service;

import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    <T extends NotificationDto> Response<Boolean> sendNotification(T dto);

    Response<Boolean> deleteNotification(String jwt, Long id);

    <T extends NotificationDto> Response<Page<T>> findAll(Pageable pageable);

    <T extends NotificationDto> Response<Page<T>> findAllByUser(String jwt);

    Response<? extends NotificationDto> getNotificationById(Long id);

}
