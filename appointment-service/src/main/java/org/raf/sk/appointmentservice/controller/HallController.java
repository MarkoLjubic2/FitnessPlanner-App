package org.raf.sk.appointmentservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Schedulable;
import org.raf.sk.appointmentservice.dto.hall.CreateHallDto;
import org.raf.sk.appointmentservice.dto.hall.HallDto;
import org.raf.sk.appointmentservice.dto.hall.UpdateHallDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.security.CheckSecurity;
import org.raf.sk.appointmentservice.service.AppointmentService;
import org.raf.sk.appointmentservice.service.Response;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;
import org.raf.sk.appointmentservice.service.combinator.FilterJSON;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.DayOfWeek;
import java.util.Optional;

@RestController
@RequestMapping("/halls")
@AllArgsConstructor
public class HallController {

    private final AppointmentService appointmentService;

    @ApiOperation(value = "Get all halls")
    @GetMapping
    @CheckSecurity(roles = {"ADMIN", "USER", "MANAGER"})
    public ResponseEntity<Response<Page<HallDto>>> getHalls(@ApiIgnore Pageable pageable) {
        return new ResponseEntity<>(appointmentService.findAllHalls(pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Get hall by id")
    @GetMapping("/{id}")
    @CheckSecurity(roles = {"ADMIN", "USER", "MANAGER"})
    public ResponseEntity<Response<HallDto>> getHall(@PathVariable("id") Long id) {
        return new ResponseEntity<>(appointmentService.findHallById(id), HttpStatus.OK);
    }

    @ApiOperation(value = "Create hall")
    @PostMapping
    @CheckSecurity(roles = {"ADMIN", "MANAGER"})
    public ResponseEntity<Response<Boolean>> createHall(@RequestHeader("Authorization") String jwt, @RequestBody CreateHallDto createHallDto) {
        return new ResponseEntity<>(appointmentService.createHall(jwt, createHallDto), HttpStatus.OK);
    }

    @ApiOperation(value = "Update hall")
    @PutMapping
    @CheckSecurity(roles = {"ADMIN", "MANAGER"})
    public ResponseEntity<Response<Boolean>> updateHall(@RequestHeader("Authorization") String jwt, @RequestBody UpdateHallDto updateHallDto) {
        return new ResponseEntity<>(appointmentService.updateHall(jwt, updateHallDto), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete hall")
    @DeleteMapping
    @CheckSecurity(roles = {"ADMIN", "MANAGER"})
    public ResponseEntity<Response<Boolean>> deleteHall(@RequestHeader("Authorization") String jwt, @RequestParam("hallId") Long hallId) {
        return new ResponseEntity<>(appointmentService.deleteHall(jwt, hallId), HttpStatus.OK);
    }

    @ApiOperation(value = "Get filtered reservations")
    @GetMapping("/reservations")
    public ResponseEntity<Response<Page<ReservationDto>>> getReservations(@ApiIgnore Pageable pageable, @RequestParam("filter") String filter) {
        FilterCombinator<Schedulable> filterCombinator = convertFromJson(filter);
        System.out.println("filterCombinator: "+ filterCombinator);
        return new ResponseEntity<>(appointmentService.findReservationByFilter(filterCombinator, pageable), HttpStatus.OK);
    }

    public FilterCombinator<Schedulable> convertFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        FilterJSON filterJSON;
        try {
            filterJSON = objectMapper.readValue(json, FilterJSON.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        FilterCombinator<Schedulable> filterCombinator = schedulable -> true;

        Optional.ofNullable(filterJSON.getType())
                .ifPresent(type -> filterCombinator.and(FilterCombinator.isType(type)));
        Optional.ofNullable(filterJSON.getDay())
                .ifPresent(day -> filterCombinator.and(FilterCombinator.isDay(DayOfWeek.valueOf(day.name()))));
        Optional.ofNullable(filterJSON.getIndividual())
                .ifPresent(individual -> filterCombinator.and(individual ? FilterCombinator.isIndividualTraining() : FilterCombinator.isGroupTraining()));

        return filterCombinator;
    }

}
