package org.raf.sk.notificationservice.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.raf.sk.notificationservice.security.CheckSecurity;
import org.raf.sk.notificationservice.service.NotificationService;
import org.raf.sk.notificationservice.service.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/notification")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @ApiOperation(value = "All notifications")
    @GetMapping
    @CheckSecurity(roles = {"ADMIN"})
    public ResponseEntity<Response<Page<NotificationDto>>> getAllNotifications(Pageable pageable) {
        Response<Page<NotificationDto>> response = notificationService.findAll(pageable);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Get notification")
    @GetMapping("/{id}")
    public ResponseEntity<Response<? extends NotificationDto>> getNotificationById(@ApiParam(value = "id", required = true) @PathVariable Long id) {
        Response<? extends NotificationDto> response = notificationService.getNotificationById(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Send notification")
    @PostMapping
    public ResponseEntity<Response<Boolean>> sendNotification(@ApiParam(value = "Notification to send", required = true)
                                                                  @RequestBody NotificationDto notificationDto, @RequestParam String typeName) {
        Response<Boolean> response = notificationService.sendNotification(typeName, notificationDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Delete notification")
    @DeleteMapping("/{id}")
    @CheckSecurity(roles = {"ADMIN"})
    public ResponseEntity<Boolean> deleteNotification(@RequestHeader("Authorization") String jwt, @ApiParam(value = "id", required = true) @PathVariable Long id) {
        Response<Boolean> response = notificationService.deleteNotification(id);
        return new ResponseEntity<>(response.getData(), HttpStatus.valueOf(response.getStatusCode()));
    }

}

