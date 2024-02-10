package org.raf.sk.appointmentservice.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.domain.Schedulable;
import org.raf.sk.appointmentservice.dto.appointment.AppointmentDto;
import org.raf.sk.appointmentservice.dto.reservation.ReservationDto;
import org.raf.sk.appointmentservice.security.CheckSecurity;
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

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.HEAD,
        RequestMethod.OPTIONS
})
@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {

    private final AppointmentService appointmentService;
    private final JSONController JSONController;

    @ApiOperation(value = "Get all reservations")
    @GetMapping
    @CheckSecurity(roles = {"ADMIN", "MANAGER", "USER"})
    public ResponseEntity<Response<Page<ReservationDto>>> findAllReservations(@RequestHeader("Authorization") String jwt, @ApiIgnore Pageable pageable) {
        return new ResponseEntity<>(appointmentService.findAllReservations(pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Get filtered reservations")
    @GetMapping("/filter")
    @CheckSecurity(roles = {"ADMIN", "MANAGER", "USER"})
    public ResponseEntity<Response<Page<ReservationDto>>> findReservationByFilter(@RequestHeader("Authorization") String jwt,
                                                                                  @ApiIgnore Pageable pageable, @RequestParam("filter") String filter) {
        FilterCombinator<Schedulable> filterCombinator = JSONController.convertFromJson(filter);
        return new ResponseEntity<>(appointmentService.findReservationByFilter(filterCombinator, pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Schedule reservation")
    @PostMapping("/schedule")
    @CheckSecurity(roles = {"ADMIN", "MANAGER", "USER"})
    public ResponseEntity<Response<Boolean>> scheduleReservation(@RequestHeader("Authorization") String jwt, @RequestBody AppointmentDto appointmentDto) {
        return new ResponseEntity<>(appointmentService.scheduleReservation(appointmentDto), HttpStatus.OK);
    }

    @ApiOperation(value = "Cancel reservation")
    @PostMapping("/cancel")
    @CheckSecurity(roles = {"ADMIN", "MANAGER", "USER"})
    public ResponseEntity<Response<Boolean>> cancelReservation(@RequestHeader("Authorization") String jwt, @RequestBody ReservationDto reservationDto) {
        return new ResponseEntity<>(appointmentService.cancelReservation(jwt, reservationDto), HttpStatus.OK);
    }

    @ApiOperation(value = "Get reservation by manager")
    @GetMapping("/manager")
    @CheckSecurity(roles = {"ADMIN", "MANAGER", "USER"})
    public ResponseEntity<Response<Page<ReservationDto>>> getReservationByManager(@RequestHeader("Authorization") String jwt) {
        return new ResponseEntity<>(appointmentService.getReservationByManager(jwt), HttpStatus.OK);
    }
}
