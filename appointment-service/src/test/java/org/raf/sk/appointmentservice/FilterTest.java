package org.raf.sk.appointmentservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.raf.sk.appointmentservice.domain.Appointment;
import org.raf.sk.appointmentservice.domain.Schedulable;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.repository.AppointmentRepository;
import org.raf.sk.appointmentservice.service.Response;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;
import org.raf.sk.appointmentservice.service.services.SearchHandler;
import org.raf.sk.appointmentservice.util.ObjectMother;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Stream;

import static org.raf.sk.appointmentservice.constants.Constants.STATUS_NOT_FOUND;
import static org.raf.sk.appointmentservice.constants.Constants.STATUS_OK;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FilterTest {

    @Autowired
    private SearchHandler searchHandler;

    @MockBean
    private AppointmentRepository appointmentRepository;

    @ParameterizedTest
    @MethodSource("provideFilters")
    public void findAppointmentByFilter(FilterCombinator<Schedulable> filter, int status, String message, int totalFound) {
        // Arrange
        Pageable pageable = Mockito.mock(Pageable.class);
        List<Appointment> appointments = ObjectMother.createAppointmentSample();
        Page<Appointment> page = new PageImpl<>(appointments, pageable, appointments.size());
        Mockito.when(appointmentRepository.findAll(pageable)).thenReturn(page);

        // Act
        Response<Page<AppointmentDto>> response = searchHandler.findAppointmentByFilter(filter, pageable);

        // Assert
        Assertions.assertEquals(status, response.getStatusCode());
        Assertions.assertEquals(message, response.getMessage());
        Assertions.assertEquals(totalFound, response.getData().getTotalElements());
    }

    private static Stream<Arguments> provideFilters() {
        return Stream.of(
                // Test 1: No appointments found
                Arguments.of(
                        FilterCombinator
                                .isDay("Wednesday")
                                .and(FilterCombinator.isIndividualTraining())
                                .and(FilterCombinator.isType("Box")),
                        STATUS_NOT_FOUND,
                        "Appointments not found",
                        0
                ),
                // Test 2: All filters applied
                Arguments.of(
                        FilterCombinator
                                .isDay("Wednesday")
                                .and(FilterCombinator.isIndividualTraining())
                                .and(FilterCombinator.isType("Calisthenics")),
                        STATUS_OK,
                        "Appointments found",
                        1
                ),
                // Test 3: Mixed filters
                Arguments.of(
                        FilterCombinator
                                .isDay("Monday")
                                .and(FilterCombinator.isIndividualTraining()),
                        STATUS_OK,
                        "Appointments found",
                        3
                ),
                // Test 4: No filters
                Arguments.of(
                        FilterCombinator.initialize(),
                        STATUS_OK,
                        "Appointments found",
                        5
                )
        );
    }
}