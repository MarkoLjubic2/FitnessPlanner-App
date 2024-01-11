package org.raf.sk.notificationservice.service.factory;

import org.raf.sk.notificationservice.domain.Type;
import org.raf.sk.notificationservice.dto.PasswordChangeDto;

public class ChangePasswordBuilder implements MessageBuilder<PasswordChangeDto> {

    @Override
    public String build(Type type, PasswordChangeDto dto) {
        return type.getTemplate()
                .replace("%USERNAME", dto.getUsername());
    }

}