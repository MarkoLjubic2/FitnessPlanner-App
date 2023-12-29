package org.raf.sk.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.dto.ActivationDto;
import org.raf.sk.notificationservice.dto.AppointmentReservationDto;
import org.raf.sk.notificationservice.dto.ChangePasswordDto;
import org.raf.sk.notificationservice.dto.NotificationMQ;
import org.raf.sk.notificationservice.listener.factory.NotificationHandlerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
@AllArgsConstructor
public class EmailListener {

    private MessageHelper messageHelper;
    private NotificationHandlerFactory factory;
    private ObjectMapper objectMapper;

    @JmsListener(destination = "${async.sendEmails}", concurrency = "5-10")
    public void sendNotification(Message message) throws JMSException {
        NotificationMQ<?> notificationMQ = messageHelper.getMessage(message, NotificationMQ.class);

        switch (notificationMQ.getType()) {
            case "ACTIVATION":
                factory.getHandler("ACTIVATION").handle(objectMapper.convertValue(notificationMQ.getData(), ActivationDto.class));
                break;
            case "CHANGE_PASSWORD":
                factory.getHandler("CHANGE_PASSWORD").handle(objectMapper.convertValue(notificationMQ.getData(), ChangePasswordDto.class));
                break;
            case "RESERVATION":
                factory.getHandler("RESERVATION").handle(objectMapper.convertValue(notificationMQ.getData(), AppointmentReservationDto.class));
                break;
            default:
                throw new IllegalArgumentException("Unknown message type: " + notificationMQ.getType());
        }
    }

}