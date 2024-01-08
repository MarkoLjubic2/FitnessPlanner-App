package org.raf.sk.appointmentservice.mapper;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Appointment;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.repository.TrainingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class AppointmentMapper {

    private final TrainingRepository trainingRepository;

    public AppointmentDto appointmentToAppointmentDto(Appointment appointment) {
        return Optional.ofNullable(appointment)
                .map(a -> {
                    AppointmentDto appointmentDto = new AppointmentDto();
                    appointmentDto.setId(a.getId());
                    appointmentDto.setDate(a.getDate());
                    appointmentDto.setStartTime(a.getStartTime());
                    appointmentDto.setEndTime(a.getEndTime());
                    appointmentDto.setTrainingId(a.getTraining().getId());
                    appointmentDto.setMaxClients(a.getMaxClients());
                    appointmentDto.setCurrentClients(a.getCurrentClients());
                    appointmentDto.setDay(a.getDay());
                    appointmentDto.setOpen(a.isOpen());
                    return appointmentDto;
                })
                .orElse(null);
    }

}
