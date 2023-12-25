package org.raf.sk.notificationservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;

@Getter
@Setter
public class ActivationDto extends NotificationDto {

    private String link;
    private String clientFirstName;
    private String clientLastName;

}