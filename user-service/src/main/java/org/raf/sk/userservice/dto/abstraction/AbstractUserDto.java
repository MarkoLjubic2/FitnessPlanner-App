package org.raf.sk.userservice.dto.abstraction;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@Setter
public abstract class AbstractUserDto {

    private Long id;
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    private String username;
    private String password;
    @Email(message = "Email should be valid")
    private String email;
    private String dateOfBirth;
    private String firstName;
    private String lastName;

}
