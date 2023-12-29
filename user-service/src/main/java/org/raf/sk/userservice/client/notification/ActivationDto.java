package org.raf.sk.userservice.client.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.raf.sk.userservice.client.notification.abstraction.NotificationDto;

@Getter
@Setter
@AllArgsConstructor
public class ActivationDto extends NotificationDto {

    private String username;
    private String token;

    public ActivationDto(String mail, String username, String token) {
        this.mail = mail;
        this.username = username;
        this.token = token;
    }

}
