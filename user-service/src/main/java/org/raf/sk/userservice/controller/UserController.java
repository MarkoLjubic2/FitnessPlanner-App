package org.raf.sk.userservice.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.raf.sk.userservice.client.appointment.AppointmentUserDto;
import org.raf.sk.userservice.dto.*;
import org.raf.sk.userservice.security.CheckSecurity;
import org.raf.sk.userservice.service.Response;
import org.raf.sk.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.HEAD,
        RequestMethod.OPTIONS
})
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @ApiOperation(value = "Get all users")
    @GetMapping
    @CheckSecurity(roles = {"ADMIN"})
    public ResponseEntity<Response<Page<UserDto>>> getAllUsers(@RequestHeader("Authorization") String jwt, Pageable pageable) {
        Response<Page<UserDto>> response = userService.findAll(jwt, pageable);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Check if user exists")
    @GetMapping("/userExists/{userId}")
    @CheckSecurity(roles = {"ADMIN", "USER", "MANAGER"})
    public ResponseEntity<Response<Boolean>> userExists(@PathVariable Long userId) {
        Response<Boolean> response = userService.userExists(userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Get user data")
    @GetMapping("/getUserData/{userId}")
    public ResponseEntity<Response<UserDto>> getUserData(@PathVariable("userId") Long userId) {
        Response<UserDto> response = userService.getUserData(userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Change total sessions")
    @PutMapping("/changeTotalSessions/{userId}")
    public ResponseEntity<Response<Boolean>> changeTotalSessions(@PathVariable("userId") Long userId, @RequestParam int value) {
        Response<Boolean> response = userService.changeTotalSessions(userId, value);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Ban user")
    @CheckSecurity(roles = {"ADMIN"})
    @PutMapping("/banUser/{username}")
    public ResponseEntity<Response<Boolean>> banUser(@RequestHeader("Authorization") String jwt, @PathVariable("username") String username) {
        Response<Boolean> response = userService.banUser(jwt, username);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Unban user")
    @CheckSecurity(roles = {"ADMIN"})
    @PutMapping("/unbanUser/{username}")
    public ResponseEntity<Response<Boolean>> unbanUser(@RequestHeader("Authorization") String jwt, @PathVariable("username") String username) {
        Response<Boolean> response = userService.unbanUser(jwt, username);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Add user")
    @PostMapping("/addUser")
    public ResponseEntity<Response<Boolean>> addUser(@RequestBody CreateUserDto createUserDto) {
        Response<Boolean> response = userService.addUser(createUserDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Add manager")
    @CheckSecurity(roles = {"ADMIN"})
    @PostMapping("/addManager")
    public ResponseEntity<Response<Boolean>> addManager(@RequestBody CreateManagerDto createManagerDto) {
        Response<Boolean> response = userService.addManager(createManagerDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Update user")
    @CheckSecurity(roles = {"ADMIN", "USER"})
    @PutMapping("/updateUser")
    public ResponseEntity<Response<Boolean>> updateUser(@RequestHeader("Authorization") String jwt, @RequestBody UpdateUserDto updateUserDto) {
        Response<Boolean> response = userService.updateUser(jwt, updateUserDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Update manager")
    @CheckSecurity(roles = {"ADMIN", "MANAGER"})
    @PutMapping("/updateManager")
    public ResponseEntity<Response<Boolean>> updateManager(@RequestHeader("Authorization") String jwt, @RequestBody UpdateManagerDto updateManagerDto) {
        Response<Boolean> response = userService.updateManager(jwt, updateManagerDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Change Password")
    @PutMapping("/changePassword")
    public ResponseEntity<Response<Boolean>> changePassword(@RequestHeader("Authorization") String jwt, @RequestBody ChangePasswordDto changePasswordDto) {
        Response<Boolean> response = userService.changePassword(jwt, changePasswordDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Login")
    @PostMapping("/login")
    public ResponseEntity<Response<TokenResponseDto>> login(@RequestBody TokenRequestDto tokenRequestDto) {
        Response<TokenResponseDto> response = userService.login(tokenRequestDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Activate")
    @GetMapping("/activate")
    public ResponseEntity<Response<Boolean>> verifyUser(@RequestParam String token) {
        Response<Boolean> response = userService.verifyUser(token);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    // Routes for communication with other services

    @ApiOperation(value = "Get appointment user data")
    @GetMapping("/getAppointmentUserData/{userId}")
    public ResponseEntity<Response<AppointmentUserDto>> getAppointmentUserData(@PathVariable("userId") Long userId) {
        Response<AppointmentUserDto> response = userService.getAppointmentUserData(userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Get manager data")
    @GetMapping("/getManagerData/{userId}")
    public ResponseEntity<Response<ManagerDto>> getManagerData(@PathVariable("userId") Long userId) {
        Response<ManagerDto> response = userService.getManagerData(userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

}
