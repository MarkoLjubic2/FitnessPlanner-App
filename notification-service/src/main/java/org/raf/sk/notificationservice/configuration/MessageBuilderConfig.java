package org.raf.sk.notificationservice.configuration;

import org.raf.sk.notificationservice.dto.ActivationDto;
import org.raf.sk.notificationservice.dto.AppointmentReservationDto;
import org.raf.sk.notificationservice.dto.PasswordChangeDto;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.raf.sk.notificationservice.service.factory.ActivationBuilder;
import org.raf.sk.notificationservice.service.factory.ChangePasswordBuilder;
import org.raf.sk.notificationservice.service.factory.MessageBuilder;
import org.raf.sk.notificationservice.service.factory.ReservationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MessageBuilderConfig {

    @Bean
    public Map<Class<? extends NotificationDto>, MessageBuilder<? extends NotificationDto>> messageBuilders() {
        Map<Class<? extends NotificationDto>, MessageBuilder<? extends NotificationDto>> messageBuilders = new HashMap<>();

        messageBuilders.put(ActivationDto.class, new ActivationBuilder());
        messageBuilders.put(AppointmentReservationDto.class, new ReservationBuilder());
        messageBuilders.put(PasswordChangeDto.class, new ChangePasswordBuilder());

        return messageBuilders;
    }

}
