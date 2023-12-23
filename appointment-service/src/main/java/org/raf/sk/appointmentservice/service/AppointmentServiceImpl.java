package org.raf.sk.appointmentservice.service;

import org.raf.sk.appointmentservice.dto.hall.CreateHallDto;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.raf.sk.appointmentservice.dto.hall.UpdateHallDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Override
    public Response<Page<HallDto>> findAllHalls(Pageable pageable) {
        return null;
    }

    @Override
    public Response<HallDto> findHallById(Long hallId) {
        return null;
    }

    @Override
    public Response<Boolean> createHall(String jwt, CreateHallDto createHallDto) {
        return null;
    }

    @Override
    public Response<Boolean> updateHall(String jwt, UpdateHallDto updateHallDto) {
        return null;
    }

    @Override
    public Response<Boolean> deleteHall(String jwt, Long hallId) {
        return null;
    }

}
