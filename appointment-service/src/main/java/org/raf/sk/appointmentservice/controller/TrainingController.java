package org.raf.sk.appointmentservice.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.raf.sk.appointmentservice.dto.training.TrainingDto;
import org.raf.sk.appointmentservice.service.AppointmentService;
import org.raf.sk.appointmentservice.service.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/trainings")
@AllArgsConstructor
public class TrainingController {

    private final AppointmentService appointmentService;

    @ApiOperation(value = "Get training by id")
    @GetMapping("/{id}")
    public ResponseEntity<Response<TrainingDto>> getHall(@PathVariable("id") Long id) {
        return new ResponseEntity<>(appointmentService.findTrainingById(id), HttpStatus.OK);
    }


}
