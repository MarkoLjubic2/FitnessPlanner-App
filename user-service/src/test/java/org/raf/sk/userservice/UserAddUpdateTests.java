package org.raf.sk.userservice;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.dto.CreateUserDto;
import org.raf.sk.userservice.dto.UpdateUserDto;
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

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserAddUpdateTests {

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
    public void add_user_invalid_email_test() {
        // Arrange
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("invalid_email");

        // Act
        Response<Boolean> response = userService.addUser(dto);

        // Assert
        assertUserResponse(response, STATUS_BAD_REQUEST, "Invalid email format", false);
    }

    @Test
    public void add_user_email_exists_test() {
        // Arrange
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("test@email.com");
        when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(new User()));

        // Act
        Response<Boolean> response = userService.addUser(dto);

        // Assert
        assertUserResponse(response, STATUS_BAD_REQUEST, "User with this email already exists", false);
    }

    @Test
    public void add_user_username_exists_test() {
        // Arrange
        CreateUserDto dto = new CreateUserDto();
        dto.setUsername("existing_username");
        dto.setEmail("valid@gmail.com");
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(new User()));

        // Act
        Response<Boolean> response = userService.addUser(dto);

        // Assert
        assertUserResponse(response, STATUS_BAD_REQUEST, "User with this username already exists", false);
    }

    @Test
    public void update_user_not_found_test() {
        // Arrange
        UpdateUserDto dto = new UpdateUserDto();
        dto.setUsername("username");
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.empty());
        when(tokenService.isTokenValid(any())).thenReturn(true);
        when(tokenService.parseToken(any())).thenReturn(Jwts.claims().setSubject("username"));

        // Act
        Response<Boolean> response = userService.updateUser("valid_jwt", dto);

        // Assert
        assertUserResponse(response, STATUS_NOT_FOUND, "User not found", false);
    }

    @Test
    public void update_user_invalid_jwt_test() {
        // Arrange
        UpdateUserDto dto = new UpdateUserDto();
        dto.setUsername("username");
        when(tokenService.isTokenValid(any())).thenReturn(false);
        when(tokenService.parseToken(any())).thenReturn(Jwts.claims().setSubject("username"));

        // Act
        Response<Boolean> response = userService.updateUser("invalid_jwt", dto);

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Invalid or expired token", false);
    }

    @Test
    public void update_user_unauthorized_test() {
        // Arrange
        UpdateUserDto dto = new UpdateUserDto();
        dto.setUsername("username");
        when(tokenService.isTokenValid(any())).thenReturn(true);
        when(tokenService.parseToken(any())).thenReturn(Jwts.claims().setSubject("different_username"));
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(new User()));

        // Act
        Response<Boolean> response = userService.updateUser("valid_jwt", dto);

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Unauthorized", false);
    }

    @Test
    public void update_user_test() {
        // Arrange
        UpdateUserDto dto = new UpdateUserDto();
        dto.setUsername("username");
        User user = new User();
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(user));
        when(tokenService.isTokenValid(any())).thenReturn(true);
        when(tokenService.parseToken(any())).thenReturn(Jwts.claims().setSubject("username"));

        // Act
        Response<Boolean> response = userService.updateUser("valid_jwt", dto);

        // Assert
        assertUserResponse(response, STATUS_OK, "User updated", true);
    }

}
