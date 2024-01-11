package org.raf.sk.notificationservice.service;

import org.raf.sk.notificationservice.dto.MailDto;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    <T extends NotificationDto> Response<Boolean> sendNotification(String typeName, T dto);

    Response<Page<MailDto>> findAll(String jwt, Pageable pageable);

    Response<Boolean> deleteNotification(String jwt, Long id);

    Response<Page<MailDto>> findAllByUser(String jwt, Pageable pageable);

}
