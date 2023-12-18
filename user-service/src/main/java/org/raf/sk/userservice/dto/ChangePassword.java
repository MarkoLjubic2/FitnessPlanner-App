package org.raf.sk.userservice.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class ChangePassword {

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String oldPassword;
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

}
