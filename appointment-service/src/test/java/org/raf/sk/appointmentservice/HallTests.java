package org.raf.sk.appointmentservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.raf.sk.appointmentservice.mapper.HallMapper;
import org.raf.sk.appointmentservice.repository.HallRepository;
import org.raf.sk.appointmentservice.service.Response;
import org.raf.sk.appointmentservice.service.services.HallHandler;
import org.raf.sk.appointmentservice.util.TestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_NOT_FOUND;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_OK;

@ExtendWith(MockitoExtension.class)
public class HallTests {

    @InjectMocks
    private HallHandler hallHandler;
    @Mock
    private HallRepository hallRepository;
    @Mock
    private HallMapper hallMapper;

    @BeforeEach
    public void setUp() {
        Mockito.reset(hallRepository, hallMapper);
    }

    @Test
    public void find_all_halls_test() {
        // Arrange
        Hall h1 = new Hall();
        Hall h2 = new Hall();
        Page<Hall> hallsPage = new PageImpl<>(List.of(h1, h2));
        when(hallRepository.findAll(any(Pageable.class))).thenReturn(hallsPage);
        HallDto dto1 = hallMapper.hallToHallDto(h1);
        HallDto dto2 = hallMapper.hallToHallDto(h1);
        when(hallMapper.hallToHallDto(h1)).thenReturn(dto1);
        when(hallMapper.hallToHallDto(h2)).thenReturn(dto2);

        // Act
        Response<Page<HallDto>> response = hallHandler.findAllHalls(PageRequest.of(0, 10));

        // Assert
        TestUtils.assertResponse(response, STATUS_OK, "All halls", hallsPage.map(hallMapper::hallToHallDto));

    }

    @Test
    public void find_hall_by_id_test() {
        // Arrange
        Hall hall = new Hall();
        hall.setId(1L);
        when(hallRepository.getHallById(1L)).thenReturn(Optional.of(hall));
        HallDto hallDto = hallMapper.hallToHallDto(hall);
        when(hallMapper.hallToHallDto(hall)).thenReturn(hallDto);

        // Act
        Response<HallDto> response = hallHandler.findHallById(1L);

        // Assert
        TestUtils.assertResponse(response, STATUS_OK, "Hall found", hallDto);
    }

    @Test
    public void find_hall_by_id_not_found_test() {
        // Arrange
        Hall h1 = new Hall();
        when(hallRepository.getHallById(any(Long.class))).thenReturn(Optional.empty());

        // Act
        Response<HallDto> response = hallHandler.findHallById(1L);

        // Assert
        TestUtils.assertResponse(response, STATUS_NOT_FOUND, "Hall not found", null);
    }

}
