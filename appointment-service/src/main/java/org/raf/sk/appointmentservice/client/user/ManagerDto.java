package org.raf.sk.appointmentservice.client.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import java.util.Date;

@Getter
@Setter
public class ManagerDto {

    private Long id;
    private String username;
    @Email(message = "Email should be valid")
    private String email;
    private String firstName;
    private String lastName;
    private Long hallId;
    private Date hireDate;

}
