package org.raf.sk.appointmentservice.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Schedulable;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.service.AppointmentService;
import org.raf.sk.appointmentservice.service.Response;
import org.raf.sk.appointmentservice.service.combinator.FilterCombinator;
import org.raf.sk.appointmentservice.utils.JSONController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@CrossOrigin
@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {

    private final AppointmentService appointmentService;
    private final JSONController JSONController;

    @ApiOperation(value = "Get all reservations")
    @GetMapping
    public ResponseEntity<Response<Page<ReservationDto>>> findAllReservations(@ApiIgnore Pageable pageable) {
        return new ResponseEntity<>(appointmentService.findAllReservations(pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Get filtered reservations")
    @GetMapping("/filter")
    public ResponseEntity<Response<Page<ReservationDto>>> findReservationByFilter(@ApiIgnore Pageable pageable, @RequestParam("filter") String filter) {
        FilterCombinator<Schedulable> filterCombinator = JSONController.convertFromJson(filter);
        return new ResponseEntity<>(appointmentService.findReservationByFilter(filterCombinator, pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Schedule reservation")
    @PostMapping("/schedule")
    public ResponseEntity<Response<Boolean>> scheduleReservation(@RequestBody AppointmentDto appointmentDto) {
        return new ResponseEntity<>(appointmentService.scheduleReservation(appointmentDto), HttpStatus.OK);
    }

    @ApiOperation(value = "Cancel reservation")
    @PostMapping("/cancel")
    public ResponseEntity<Response<Boolean>> cancelReservation(@RequestBody ReservationDto reservationDto) {
        return new ResponseEntity<>(appointmentService.cancelReservation(reservationDto), HttpStatus.OK);
    }
}
