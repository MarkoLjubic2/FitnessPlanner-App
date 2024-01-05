package org.raf.sk.userservice.client.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.raf.sk.userservice.client.notification.abstraction.NotificationDto;

@Getter
@Setter
@AllArgsConstructor
public class PasswordChangeDto extends NotificationDto {

    private String username;

    public PasswordChangeDto(String username, String mail) {
        this.mail = mail;
        this.username = username;
    }
}
