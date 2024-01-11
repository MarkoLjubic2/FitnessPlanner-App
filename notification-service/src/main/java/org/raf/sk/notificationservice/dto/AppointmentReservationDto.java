package org.raf.sk.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentReservationDto extends NotificationDto {

    private String managerEmail;
    private String clientFirstName;
    private String clientLastName;
    private String hallName;
    private int price;

}
