package org.raf.sk.notificationservice.mapper;


import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.domain.Notification;
import org.raf.sk.notificationservice.dto.MailDto;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
@AllArgsConstructor
public class NotificationMapper {

    public MailDto notificationToMailDto(Notification notification) {
        return Optional.ofNullable(notification)
                .map(n -> {
                    MailDto mailDto = new MailDto();
                    mailDto.setMail(n.getMail());
                    mailDto.setTypeId(n.getType().getId());
                    mailDto.setBody(n.getBody());
                    mailDto.setTime(n.getTime());
                    return mailDto;
                })
                .orElse(null);
    }

}
