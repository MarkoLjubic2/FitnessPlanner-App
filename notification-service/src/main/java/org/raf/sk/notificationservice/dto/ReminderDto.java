package org.raf.sk.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReminderDto {

    private Long typeId;
    private String clientEmail;
    private String managerEmail;
    private String clientFirstName;
    private String clientLastName;
    private String hallName;

}
