package org.raf.sk.appointmentservice.client.notification;

import lombok.Getter;
import lombok.Setter;
import org.raf.sk.appointmentservice.client.notification.abstraction.NotificationDto;

@Getter
@Setter
public class ReminderDto extends NotificationDto {

    private String clientFirstName;
    private String clientLastName;
    private String hallName;

    public ReminderDto(String mail, String clientFirstName, String clientLastName, String hallName) {
        super(mail);
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.hallName = hallName;
    }

}
