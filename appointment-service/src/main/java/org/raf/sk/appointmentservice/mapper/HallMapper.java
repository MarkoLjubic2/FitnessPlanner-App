package org.raf.sk.appointmentservice.mapper;

import lombok.NoArgsConstructor;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.dto.hall.CreateHallDto;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@NoArgsConstructor
public class HallMapper {

    public HallDto hallToHallDto(Hall hall) {
        return Optional.ofNullable(hall)
                .map(h -> {
                    HallDto hallDto = new HallDto();
                    hallDto.setId(h.getId());
                    hallDto.setName(h.getName());
                    hallDto.setDescription(h.getDescription());
                    hallDto.setCoaches(h.getCoaches());
                    return hallDto;
                })
                .orElse(null);
    }

    public Hall hallDtoToHall(HallDto hallDto) {
        return Optional.ofNullable(hallDto)
                .map(dto -> {
                    Hall hall = new Hall();
                    hall.setId(dto.getId());
                    hall.setName(dto.getName());
                    hall.setDescription(dto.getDescription());
                    hall.setCoaches(dto.getCoaches());
                    return hall;
                })
                .orElse(null);
    }

    public CreateHallDto hallToCreateHallDto(Hall hall) {
        return Optional.ofNullable(hall)
                .map(h -> {
                    CreateHallDto createHallDto = new CreateHallDto();
                    createHallDto.setName(h.getName());
                    createHallDto.setDescription(h.getDescription());
                    createHallDto.setCoaches(h.getCoaches());
                    return createHallDto;
                })
                .orElse(null);
    }

    public Hall createHallDtoToHall(CreateHallDto createHallDto) {
        return Optional.ofNullable(createHallDto)
                .map(dto -> {
                    Hall hall = new Hall();
                    hall.setName(dto.getName());
                    hall.setDescription(dto.getDescription());
                    hall.setCoaches(dto.getCoaches());
                    return hall;
                })
                .orElse(null);
    }

}
