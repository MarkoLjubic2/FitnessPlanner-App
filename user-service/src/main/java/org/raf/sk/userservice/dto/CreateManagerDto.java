package org.raf.sk.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.raf.sk.userservice.dto.abstraction.AbstractUserDto;

@Getter
@Setter
public class CreateManagerDto extends AbstractUserDto {

    private Long hallId;
    private String hireDate;

}
