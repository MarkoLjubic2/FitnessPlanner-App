package org.raf.sk.appointmentservice.client.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.raf.sk.appointmentservice.client.notification.abstraction.NotificationDto;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentReservationDto extends NotificationDto {

    private String managerEmail;
    private String clientEmail;
    private String clientFirstName;
    private String clientLastName;
    private String hallName;
    private int price;

}
