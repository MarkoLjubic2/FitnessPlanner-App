package org.raf.sk.appointmentservice.mapper;

import lombok.NoArgsConstructor;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.dto.CreateHallDto;
import org.raf.sk.appointmentservice.dto.HallDto;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class HallMapper {

    public HallDto hallToHallDto(Hall hall) {
        HallDto hallDto = new HallDto();
        hallDto.setId(hall.getId());
        hallDto.setName(hall.getName());
        hallDto.setDescription(hall.getDescription());
        hallDto.setCoaches(hall.getCoaches());
        return hallDto;
    }

    public Hall hallDtoToHall(HallDto hallDto) {
        Hall hall = new Hall();
        hall.setId(hallDto.getId());
        hall.setName(hallDto.getName());
        hall.setDescription(hallDto.getDescription());
        hall.setCoaches(hallDto.getCoaches());
        return hall;
    }

    public CreateHallDto hallToCreateHallDto(Hall hall) {
        CreateHallDto createHallDto = new CreateHallDto();
        createHallDto.setName(hall.getName());
        createHallDto.setDescription(hall.getDescription());
        createHallDto.setCoaches(hall.getCoaches());
        return createHallDto;
    }

    public Hall createHallDtoToHall(CreateHallDto createHallDto) {
        Hall hall = new Hall();
        hall.setName(createHallDto.getName());
        hall.setDescription(createHallDto.getDescription());
        hall.setCoaches(createHallDto.getCoaches());
        return hall;
    }

}
