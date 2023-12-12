package org.raf.sk.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.raf.sk.userservice.dto.abstraction.AbstractUserDto;

@Getter
@Setter
public class CreateUserDto extends AbstractUserDto {

    private String licenceID;
    private int totalSessions;


}
