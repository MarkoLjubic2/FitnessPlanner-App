package org.raf.sk.appointmentservice.client.notification;

import lombok.Getter;
import lombok.Setter;
import org.raf.sk.appointmentservice.client.notification.abstraction.NotificationDto;

@Getter
@Setter
public class AppointmentReservationDto extends NotificationDto {

    private String managerEmail;
    private String clientFirstName;
    private String clientLastName;
    private String hallName;
    private int price;

    public AppointmentReservationDto(String mail, String managerEmail, String clientFirstName, String clientLastName, String hallName, int price) {
        super(mail);
        this.managerEmail = managerEmail;
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.hallName = hallName;
        this.price = price;
    }
}
