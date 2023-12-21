package org.raf.sk.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActivationDto {

    private Long typeId;
    private String clientEmail;
    private String link;
    private String clientFirstName;
    private String clientLastName;

}
