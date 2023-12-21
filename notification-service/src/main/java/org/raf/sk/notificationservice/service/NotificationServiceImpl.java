package org.raf.sk.notificationservice.service;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService{

    @Override
    public <T> Response<Boolean> sendNotification(T dto) {
        return null;
    }
}
