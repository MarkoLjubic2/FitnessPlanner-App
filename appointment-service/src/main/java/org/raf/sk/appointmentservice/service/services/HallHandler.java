package org.raf.sk.appointmentservice.service.services;

import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.dto.hall.CreateHallDto;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.raf.sk.appointmentservice.dto.hall.UpdateHallDto;
import org.raf.sk.appointmentservice.mapper.HallMapper;
import org.raf.sk.appointmentservice.repository.HallRepository;
import org.raf.sk.appointmentservice.security.tokenService.TokenService;
import org.raf.sk.appointmentservice.service.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.raf.sk.appointmentservice.constants.Constants.*;

@Component
@AllArgsConstructor
public class HallHandler {

    private final HallRepository hallRepository;
    private final HallMapper hallMapper;
    private final TokenService tokenService;

    public Response<Page<HallDto>> findAllHalls(Pageable pageable) {
        return new Response<>(STATUS_OK, "All halls", hallRepository.findAll(pageable).map(hallMapper::hallToHallDto));
    }

    public Response<HallDto> findHallById(Long hallId) {
        return hallRepository.getHallById(hallId)
                .map(hall -> new Response<>(STATUS_OK, "Hall found", hallMapper.hallToHallDto(hall)))
                .orElse(new Response<>(STATUS_NOT_FOUND, "Hall not found", null));
    }

    public Response<Boolean> createHall(String jwt, CreateHallDto createHallDto) {
        if (tokenService.getRole(jwt).equals("MANAGER")) {
            Hall hall = hallMapper.createHallDtoToHall(createHallDto);
            hallRepository.save(hall);
            return new Response<>(STATUS_OK, "Hall created", true);
        }
        return new Response<>(STATUS_FORBIDDEN, "Forbidden", false);
    }

    public Response<Boolean> updateHall(String jwt, UpdateHallDto updateHallDto) {
        if (tokenService.getRole(jwt).equals("MANAGER")) {
            return hallRepository.getHallById(updateHallDto.getId())
                    .map(hall -> {
                        Optional.ofNullable(updateHallDto.getName()).ifPresent(hall::setName);
                        Optional.ofNullable(updateHallDto.getDescription()).ifPresent(hall::setDescription);
                        Optional.of(updateHallDto.getCoaches()).ifPresent(hall::setCoaches);
                        hallRepository.save(hall);
                        return new Response<>(STATUS_OK, "Hall updated", true);
                    })
                    .orElse(new Response<>(STATUS_NOT_FOUND, "Hall not found", false));
        }
        return new Response<>(STATUS_FORBIDDEN, "Forbidden", false);
    }

    public Response<Boolean> deleteHall(String jwt, Long hallId) {
        if (tokenService.getRole(jwt).equals("MANAGER")) {
            hallRepository.deleteById(hallId);
            return new Response<>(STATUS_OK, "Hall deleted", true);
        }
        return new Response<>(STATUS_FORBIDDEN, "Forbidden", false);
    }

}
