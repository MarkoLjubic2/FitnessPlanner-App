package org.raf.sk.notificationservice.listener;

import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.service.NotificationService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
@AllArgsConstructor
public class EmailListener {

    private MessageHelper messageHelper;
    private NotificationService notificationService;

    @JmsListener(destination = "${async.sendEmails}", concurrency = "5-10")
    public void sendNotification(Message message, Class<?> clazz) throws JMSException {
        notificationService.sendNotification(messageHelper.getMessage(message, clazz));
    }

}
