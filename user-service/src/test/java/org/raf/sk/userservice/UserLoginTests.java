package org.raf.sk.userservice;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raf.sk.userservice.domain.Status;
import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.dto.TokenRequestDto;
import org.raf.sk.userservice.dto.TokenResponseDto;
import org.raf.sk.userservice.mapper.UserMapper;
import org.raf.sk.userservice.repository.UserRepository;
import org.raf.sk.userservice.security.tokenService.TokenService;
import org.raf.sk.userservice.service.Response;
import org.raf.sk.userservice.service.UserServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.raf.sk.userservice.constants.Constants.STATUS_OK;

@ExtendWith(MockitoExtension.class)
public class UserLoginTests {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @Test
    public void login_successful_test() {
        // Arrange
        String username = "testUser";
        String password = "testPassword";
        String encodedPassword = "encodedTestPassword";
        String token = "testToken";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        Status status = new Status();
        status.setName("VERIFIED");
        user.setUserStatus(status);

        TokenRequestDto tokenRequestDto = new TokenRequestDto();
        tokenRequestDto.setUsername(username);
        tokenRequestDto.setPassword(password);

        Claims claims = userMapper.userToClaims(user);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(encoder.matches(password, encodedPassword)).thenReturn(true);
        when(tokenService.generate(claims)).thenReturn(token);

        // Act
        Response<TokenResponseDto> response = userService.login(tokenRequestDto);

        // Assert
        assertEquals(STATUS_OK, response.getStatusCode());
        assertEquals("User logged in", response.getMessage());
        assertEquals(token, response.getData().getToken());
    }

    @Test
    public void login_unsuccessful_wrong_username_test() {
        // Arrange
        String username = "wrongUsername";
        String password = "testPassword";

        TokenRequestDto tokenRequestDto = new TokenRequestDto();
        tokenRequestDto.setUsername(username);
        tokenRequestDto.setPassword(password);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        // Act
        Response<TokenResponseDto> response = userService.login(tokenRequestDto);

        // Assert
        assertEquals("User not found", response.getMessage());
    }

    @Test
    public void login_unsuccessful_wrong_password_test() {
        // Arrange
        String username = "testUser";
        String password = "wrongPassword";
        String encodedPassword = "encodedTestPassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        Status status = new Status();
        status.setName("VERIFIED");
        user.setUserStatus(status);

        TokenRequestDto tokenRequestDto = new TokenRequestDto();
        tokenRequestDto.setUsername(username);
        tokenRequestDto.setPassword(password);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(encoder.matches(password, encodedPassword)).thenReturn(false);

        // Act
        Response<TokenResponseDto> response = userService.login(tokenRequestDto);

        // Assert
        assertEquals("Invalid credentials", response.getMessage());
    }

    @Test
    public void login_unsuccessful_unverified_user_test() {
        // Arrange
        String username = "unverifiedUser";
        String password = "testPassword";
        String encodedPassword = "encodedTestPassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        Status status = new Status();
        status.setName("UNVERIFIED");
        user.setUserStatus(status);

        TokenRequestDto tokenRequestDto = new TokenRequestDto();
        tokenRequestDto.setUsername(username);
        tokenRequestDto.setPassword(password);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Response<TokenResponseDto> response = userService.login(tokenRequestDto);

        // Assert
        assertEquals("Status forbidden", response.getMessage());
    }

}
