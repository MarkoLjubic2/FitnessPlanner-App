package org.raf.sk.notificationservice.repository;

import org.raf.sk.notificationservice.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findAllByMail(String mail, Pageable pageable);

    Page<Notification> findAllByTime(Date startDate, Date endDate, Pageable pageable);

}
