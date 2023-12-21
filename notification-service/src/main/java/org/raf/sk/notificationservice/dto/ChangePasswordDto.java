package org.raf.sk.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangePasswordDto {

    private Long typeId;
    private String clientEmail;
    private String link;

}
