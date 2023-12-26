package org.raf.sk.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.dto.ActivationDto;
import org.raf.sk.notificationservice.dto.ChangePasswordDto;
import org.raf.sk.notificationservice.dto.NotificationMQ;
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
    private ObjectMapper objectMapper;

    @JmsListener(destination = "${async.sendEmails}", concurrency = "5-10")
    public void sendNotification(Message message) throws JMSException {
        NotificationMQ<?> notificationMQ = messageHelper.getMessage(message, NotificationMQ.class);

        switch (notificationMQ.getType()) {
            case "ACTIVATION":
                ActivationDto activationDto = objectMapper.convertValue(notificationMQ.getData(), ActivationDto.class);
                notificationService.sendNotification(activationDto);
                break;
            case "CHANGE_PASSWORD":
                ChangePasswordDto changePasswordDto = objectMapper.convertValue(notificationMQ.getData(), ChangePasswordDto.class);
                notificationService.sendNotification(changePasswordDto);
                break;
            default:
                throw new IllegalArgumentException("Unknown message type: " + notificationMQ.getType());
        }
    }

}
