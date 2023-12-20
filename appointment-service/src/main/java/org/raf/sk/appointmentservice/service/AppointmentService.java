package org.raf.sk.appointmentservice.service;

import org.raf.sk.appointmentservice.dto.CreateHallDto;
import org.raf.sk.appointmentservice.dto.HallDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {

    Response<Boolean> createHall(CreateHallDto createHallDto);

    Response<Boolean> deleteHall(Long hallId);

    Response<Page<HallDto>> findAllHalls(Pageable pageable);

    //edit hall description


}
