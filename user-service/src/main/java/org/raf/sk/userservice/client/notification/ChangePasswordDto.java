package org.raf.sk.userservice.client.notification;

import lombok.Getter;
import lombok.Setter;
import org.raf.sk.userservice.client.notification.abstraction.NotificationDto;

@Getter
@Setter
public class ChangePasswordDto extends NotificationDto {

    private String email;
    private String username;

}
