package org.raf.sk.appointmentservice.service;

import org.raf.sk.appointmentservice.dto.hall.CreateHallDto;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.raf.sk.appointmentservice.dto.hall.UpdateHallDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {

    Response<Page<HallDto>> findAllHalls(Pageable pageable);

    Response<HallDto> findHallById(Long hallId);

    Response<Boolean> createHall(String jwt, CreateHallDto createHallDto);

    Response<Boolean> updateHall(String jwt, UpdateHallDto updateHallDto);

    Response<Boolean> deleteHall(String jwt, Long hallId);

}
