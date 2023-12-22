package org.raf.sk.notificationservice.service;

import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Override
    public <T> Response<Boolean> sendNotification(T dto) {
        return null;
    }

    @Override
    public Response<Boolean> deleteNotification(Long id) {
        return null;
    }

    @Override
    public <T extends NotificationDto> Response<Page<T>> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Response<? extends NotificationDto> getNotificationById(Long id) {
        return null;
    }
}
