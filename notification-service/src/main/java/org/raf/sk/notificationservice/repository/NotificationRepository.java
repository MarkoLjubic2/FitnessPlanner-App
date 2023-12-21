package org.raf.sk.notificationservice.repository;

import org.raf.sk.notificationservice.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface NotificationRepository {

    Page<Notification> findAllByClientEmail(String clientEmail, Pageable pageable);

    Page<Notification> findAllByManagerEmail(String managerEmail, Pageable pageable);

    Page<Notification> findAllByTime(Date startDate, Date endDate, Pageable pageable);

}
