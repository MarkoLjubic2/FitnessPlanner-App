package org.raf.sk.notificationservice.service;

public interface NotificationService {

    <T> Response<Boolean> sendNotification(T dto);

}
