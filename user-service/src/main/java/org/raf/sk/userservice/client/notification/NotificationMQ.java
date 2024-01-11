package org.raf.sk.userservice.client.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotificationMQ<T> {

    private String type;
    private T data;

}
