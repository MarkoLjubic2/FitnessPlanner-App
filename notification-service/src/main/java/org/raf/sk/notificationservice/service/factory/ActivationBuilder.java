package org.raf.sk.notificationservice.service.factory;

import org.raf.sk.notificationservice.domain.Type;
import org.raf.sk.notificationservice.dto.ActivationDto;

public class ActivationBuilder implements MessageBuilder<ActivationDto> {

    @Override
    public String build(Type type, ActivationDto dto) {
        return type.getTemplate()
                .replace("%USERNAME", dto.getUsername())
                .replace("%LINK", "http://localhost:8080/user/activate?token=" + dto.getToken());
    }

}
