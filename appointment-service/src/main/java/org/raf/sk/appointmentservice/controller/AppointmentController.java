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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/appointments")
@AllArgsConstructor
public class AppointmentController {


    private final AppointmentService appointmentService;
    private final JSONController JSONController;

    @ApiOperation(value = "Get all appointments")
    @GetMapping
    public ResponseEntity<Response<Page<AppointmentDto>>> findAllAppointments(@ApiIgnore Pageable pageable) {
        return new ResponseEntity<>(appointmentService.findAllAppointments(pageable), HttpStatus.OK);
    }

    @ApiOperation(value = "Get filtered reservations")
    @GetMapping("/filter")
    public ResponseEntity<Response<Page<AppointmentDto>>> findAppointmentByFilter(@ApiIgnore Pageable pageable, @RequestParam("filter") String filter) {
        FilterCombinator<Schedulable> filterCombinator = JSONController.convertFromJson(filter);
        return new ResponseEntity<>(appointmentService.findAppointmentByFilter(filterCombinator, pageable), HttpStatus.OK);
    }


}