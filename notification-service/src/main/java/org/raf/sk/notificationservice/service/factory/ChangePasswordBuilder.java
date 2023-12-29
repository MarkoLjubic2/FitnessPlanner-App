package org.raf.sk.notificationservice.service.factory;

import org.raf.sk.notificationservice.domain.Type;
import org.raf.sk.notificationservice.dto.AppointmentReservationDto;

public class ChangePasswordBuilder implements MessageBuilder<AppointmentReservationDto> {

    @Override
    public String build(Type type, AppointmentReservationDto dto) {
        return type.getTemplate()
                .replace("%FIRST_NAME", dto.getClientFirstName())
                .replace("%LAST_NAME", dto.getClientLastName())
                .replace("%HALL", dto.getHallName());
    }

}