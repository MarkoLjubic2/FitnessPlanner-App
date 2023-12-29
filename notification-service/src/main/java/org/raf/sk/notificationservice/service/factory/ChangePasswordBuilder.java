package org.raf.sk.notificationservice.service.factory;

import org.raf.sk.notificationservice.domain.Type;
import org.raf.sk.notificationservice.dto.ChangePasswordDto;

public class ChangePasswordBuilder implements MessageBuilder<ChangePasswordDto> {

    @Override
    public String build(Type type, ChangePasswordDto dto) {
        return type.getTemplate()
                .replace("%USERNAME", dto.getUsername());
    }

}