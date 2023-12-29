package org.raf.sk.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.raf.sk.userservice.domain.Role;
import org.raf.sk.userservice.domain.Status;
import org.raf.sk.userservice.dto.abstraction.AbstractUserDto;

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
