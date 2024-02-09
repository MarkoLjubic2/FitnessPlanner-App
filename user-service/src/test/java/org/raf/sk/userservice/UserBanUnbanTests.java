package org.raf.sk.userservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raf.sk.userservice.domain.Status;
import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.listener.MessageHelper;
import org.raf.sk.userservice.mapper.UserMapper;
import org.raf.sk.userservice.repository.RoleRepository;
import org.raf.sk.userservice.repository.StatusRepository;
import org.raf.sk.userservice.repository.UserRepository;
import org.raf.sk.userservice.security.tokenService.TokenService;
import org.raf.sk.userservice.service.Response;
import org.raf.sk.userservice.service.UserServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.raf.sk.userservice.constants.Constants.*;
import static org.raf.sk.userservice.constants.Constants.STATUS_UNAUTHORIZED;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserBanUnbanTests {

    @InjectMocks
    private UserServiceImpl userService;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserMapper userMapper;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private StatusRepository statusRepository;
    @MockBean
    private MessageHelper messageHelper;

    @BeforeEach
    public void setup() {
        Mockito.reset(userRepository, tokenService, userMapper, roleRepository, statusRepository, messageHelper);
    }

    // Helper
    private <U, V> void assertUserResponse(Response<U> response, int expectedStatusCode, String expectedMessage, V expectedData) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatusCode);
        assertThat(response.getMessage()).isEqualTo(expectedMessage);
        assertThat(response.getData()).isEqualTo(expectedData);
    }

    @Test
    public void ban_user_not_found_test() {
        // Arrange
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.empty());
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.banUser("valid_jwt","username");

        // Assert
        assertUserResponse(response, STATUS_NOT_FOUND, "User not found", false);
    }

    @Test
    public void ban_user_already_banned_test() {
        // Arrange
        User user = new User();
        Status status = new Status();
        status.setName("BANNED");
        user.setUserStatus(status);
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(user));
        when(statusRepository.findStatusByName("BANNED")).thenReturn(Optional.of(new Status(1L, "BANNED")));
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.banUser("valid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_CONFLICT, "User is already banned", false);
    }

    @Test
    public void ban_user_test() {
        // Arrange
        User user = new User();
        Status status = new Status();
        status.setName("VERIFIED");
        user.setUserStatus(status);
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(user));
        when(statusRepository.findStatusByName("BANNED")).thenReturn(Optional.of(new Status(1L, "BANNED")));
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.banUser("valid_jwt","username");

        // Assert
        assertUserResponse(response, STATUS_OK, "User banned", true);
        assertThat(user.getUserStatus().getName()).isEqualTo("BANNED");
    }

    @Test
    public void unban_user_not_found_test() {
        // Arrange
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.empty());
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.unbanUser("valid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_NOT_FOUND, "User not found", false);
    }

    @Test
    public void unban_user_already_verified_test() {
        // Arrange
        User user = new User();
        Status status = new Status();
        status.setName("VERIFIED");
        user.setUserStatus(status);
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(user));
        when(statusRepository.findStatusByName("VERIFIED")).thenReturn(Optional.of(new Status(1L, "VERIFIED")));
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.unbanUser("valid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_CONFLICT, "User is not banned", false);
    }

    @Test
    public void unban_user_test() {
        // Arrange
        User user = new User();
        Status status = new Status();
        status.setName("BANNED");
        user.setUserStatus(status);
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(user));
        when(statusRepository.findStatusByName("VERIFIED")).thenReturn(Optional.of(new Status(1L, "VERIFIED")));
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.unbanUser("valid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_OK, "User unbanned", true);
        assertThat(user.getUserStatus().getName()).isEqualTo("VERIFIED");
    }

    @Test
    public void ban_user_invalid_jwt_test() {
        // Arrange
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.banUser("invalid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Invalid or expired token", false);
    }

    @Test
    public void unban_user_invalid_jwt_test() {
        // Arrange
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.unbanUser("invalid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Invalid or expired token", false);
    }

    @Test
    public void ban_user_unauthorized_test() {
        // Arrange
        when(tokenService.getRole(any())).thenReturn("USER");

        // Act
        Response<Boolean> response = userService.banUser("valid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Unauthorized", false);
    }

    @Test
    public void unban_user_unauthorized_test() {
        // Arrange
        when(tokenService.getRole(any())).thenReturn("USER");

        // Act
        Response<Boolean> response = userService.unbanUser("valid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Unauthorized", false);
    }

}
