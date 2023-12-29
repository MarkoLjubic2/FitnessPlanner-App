package org.raf.sk.notificationservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MailDto {

    private String mail;
    private Long typeId;
    private String body;
    private Date time;

}
