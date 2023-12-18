package org.raf.sk.userservice.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.raf.sk.userservice.dto.*;
import org.raf.sk.userservice.security.CheckSecurity;
import org.raf.sk.userservice.service.Response;
import org.raf.sk.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @ApiOperation(value = "Get all users")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "What page number you want", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "Number of items to return", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")})
    @GetMapping
    @CheckSecurity(roles = {"ADMIN"})
    public ResponseEntity<Response<Page<UserDto>>> getAllUsers(@RequestHeader("Authorization") String authorization, Pageable pageable) {
        Response<Page<UserDto>> response = userService.findAll(pageable);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Find user by id")
    @GetMapping("/findUser/{userId}")
    @CheckSecurity(roles = {"ADMIN", "USER", "MANAGER"})
    public ResponseEntity<Response<Boolean>> findUser(@PathVariable Long userId) {
        Response<Boolean> response = userService.findUser(userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Get user data")
    @CheckSecurity(roles = {"ADMIN", "USER", "MANAGER"})
    @GetMapping("/getUserData/{userId}")
    public ResponseEntity<Response<UserDto>> getUserData(@PathVariable("userId") Long userId) {
        Response<UserDto> response = userService.getUserData(userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Change total sessions")
    @CheckSecurity(roles = {"ADMIN"})
    @PutMapping("/changeTotalSessions/{userId}")
    public ResponseEntity<Response<Boolean>> changeTotalSessions(@RequestHeader("Authorization") String authorization, @PathVariable("userId") Long userId, @RequestParam int value) {
        Response<Boolean> response = userService.changeTotalSessions(userId, value);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Ban user")
    @CheckSecurity(roles = {"ADMIN"})
    @PutMapping("/banUser/{username}")
    public ResponseEntity<Response<Boolean>> banUser(@RequestHeader("Authorization") String authorization, @PathVariable("username") String username) {
        Response<Boolean> response = userService.banUser(username);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Unban user")
    @CheckSecurity(roles = {"ADMIN"})
    @PutMapping("/unbanUser/{username}")
    public ResponseEntity<Response<Boolean>> unbanUser(@RequestHeader("Authorization") String authorization, @PathVariable("username") String username) {
        Response<Boolean> response = userService.unbanUser(username);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @ApiOperation(value = "Add user")
    @CheckSecurity(roles = {"ADMIN", "USER", "MANAGER"}) //ko moze da doda usera?
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

    @ApiOperation(value = "Login")
    @PostMapping("/login")
    public ResponseEntity<Response<TokenResponseDto>> login(@RequestBody TokenRequestDto tokenRequestDto) {
        Response<TokenResponseDto> response = userService.login(tokenRequestDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }


}
