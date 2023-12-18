package org.raf.sk.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.raf.sk.userservice.dto.abstraction.AbstractUserDto;

import java.util.Date;

@Getter
@Setter
public class UpdateManagerDto extends AbstractUserDto {

        private String hall;
        private Date hireDate;

}
