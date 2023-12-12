package org.raf.sk.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.raf.sk.userservice.domain.Role;
import org.raf.sk.userservice.domain.Status;
import org.raf.sk.userservice.dto.abstraction.AbstractUserDto;

@Getter
@Setter
public class UserDto extends AbstractUserDto {

    private Long id;
    private Role userRole;
    private Status userStatus;
    private String licenceID;
    private int totalSessions;




}
