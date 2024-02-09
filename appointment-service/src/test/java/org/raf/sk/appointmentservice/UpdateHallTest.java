package org.raf.sk.appointmentservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.raf.sk.appointmentservice.domain.Hall;
import org.raf.sk.appointmentservice.dto.hall.UpdateHallDto;
import org.raf.sk.appointmentservice.repository.HallRepository;
import org.raf.sk.appointmentservice.security.tokenService.TokenService;
import org.raf.sk.appointmentservice.service.Response;
import org.raf.sk.appointmentservice.service.services.HallHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.raf.sk.appointmentservice.constants.Constants.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UpdateHallTest {

    @Autowired
    private HallHandler hallHandler;

    @MockBean
    private HallRepository hallRepository;
    @MockBean
    private TokenService tokenService;

    private <U, V> void assertResponse(Response<U> response, int expectedStatusCode, String expectedMessage, V expectedData) {
        Assertions.assertEquals(expectedStatusCode, response.getStatusCode());
        Assertions.assertEquals(expectedMessage, response.getMessage());
        Assertions.assertEquals(expectedData, response.getData());
    }

    @Test
    public void updateHall_success() {
        // Arrange
        UpdateHallDto updateHallDto = new UpdateHallDto();
        updateHallDto.setId(1L);
        Hall hall = new Hall();

        Mockito.when(tokenService.getRole(anyString())).thenReturn("MANAGER");
        Mockito.when(hallRepository.getHallById(updateHallDto.getId())).thenReturn(Optional.of(hall));

        // Act
        Response<Boolean> response = hallHandler.updateHall("jwt", updateHallDto);

        // Assert
        assertResponse(response, STATUS_OK, "Hall updated", true);
        Mockito.verify(hallRepository, Mockito.times(1)).save(hall);
    }

    @Test
    public void updateHall_fail() {
        // Arrange
        UpdateHallDto updateHallDto = new UpdateHallDto();
        updateHallDto.setId(1L);
        Mockito.when(tokenService.getRole(anyString())).thenReturn("USER");

        // Act
        Response<Boolean> response = hallHandler.updateHall("jwt", updateHallDto);

        // Assert
        assertResponse(response, STATUS_FORBIDDEN, "Forbidden", false);
        Mockito.verify(hallRepository, Mockito.times(0)).save(any(Hall.class));
    }

    @Test
    public void updateHall_hallNotFound() {
        // Arrange
        UpdateHallDto updateHallDto = new UpdateHallDto();
        updateHallDto.setId(1L);
        Mockito.when(tokenService.getRole(anyString())).thenReturn("MANAGER");
        Mockito.when(hallRepository.getHallById(updateHallDto.getId())).thenReturn(Optional.empty());

        // Act
        Response<Boolean> response = hallHandler.updateHall("jwt", updateHallDto);

        // Assert
        assertResponse(response, STATUS_NOT_FOUND, "Hall not found", false);
        Mockito.verify(hallRepository, Mockito.times(0)).save(any(Hall.class));
    }

    @Test
    public void updateHall_nonExistentHall() {
        // Arrange
        UpdateHallDto updateHallDto = new UpdateHallDto();
        updateHallDto.setId(1L);
        Mockito.when(tokenService.getRole(anyString())).thenReturn("MANAGER");
        Mockito.when(hallRepository.getHallById(updateHallDto.getId())).thenReturn(Optional.empty());

        // Act
        Response<Boolean> response = hallHandler.updateHall("jwt", updateHallDto);

        // Assert
        assertResponse(response, STATUS_NOT_FOUND, "Hall not found", false);
        Mockito.verify(hallRepository, Mockito.times(0)).save(any(Hall.class));
    }

}
