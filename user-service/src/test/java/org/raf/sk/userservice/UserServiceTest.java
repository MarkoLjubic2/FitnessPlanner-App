package org.raf.sk.userservice;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raf.sk.userservice.domain.Status;
import org.raf.sk.userservice.domain.User;
import org.raf.sk.userservice.dto.UpdateUserDto;
import org.raf.sk.userservice.dto.UserDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.raf.sk.userservice.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserServiceTest {

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

    // Tests
    @Test
    public void find_all_users_no_users_test() {
        // Arrange
        Page<User> usersPage = new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usersPage);

        when(tokenService.isTokenValid(any())).thenReturn(true);
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Page<UserDto>> response = userService.findAll("valid_jwt", PageRequest.of(0, 10));

        // Assert
        assertUserResponse(response, STATUS_OK, "All users", usersPage);
    }

    @Test
    public void find_all_users_test() {
        // Arrange
        User user1 = new User();
        User user2 = new User();
        Page<User> usersPage = new PageImpl<>(List.of(user1, user2));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usersPage);
        UserDto userDto1 = userMapper.userToUserDto(user1);
        UserDto userDto2 = userMapper.userToUserDto(user2);
        when(userMapper.userToUserDto(user1)).thenReturn(userDto1);
        when(userMapper.userToUserDto(user2)).thenReturn(userDto2);

        when(tokenService.isTokenValid(any())).thenReturn(true);
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Page<UserDto>> response = userService.findAll("valid_jwt", PageRequest.of(0, 10));

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(STATUS_OK);
        assertThat(response.getMessage()).isEqualTo("All users");
        assertThat(response.getData().getContent()).containsExactly(userDto1, userDto2);
    }

    @Test
    public void find_user_by_id_not_found_test() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Response<Boolean> response = userService.userExists(1L);

        // Assert
        assertUserResponse(response, STATUS_NOT_FOUND, "User not found", false);
    }

    @Test
    public void find_user_by_id_test() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        // Act
        Response<Boolean> response = userService.userExists(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(STATUS_OK);
        assertThat(response.getMessage()).isEqualTo("User found");
        assertThat(response.getData()).isTrue();
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

    @Test
    public void get_user_data_not_found_test() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Response<UserDto> response = userService.getUserData(1L);

        // Assert
        assertUserResponse(response, STATUS_NOT_FOUND, "User not found", null);
    }

    @Test
    public void get_user_data_test() {
        // Arrange
        User user = new User();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto userDto = userMapper.userToUserDto(user);
        when(userMapper.userToUserDto(user)).thenReturn(userDto);

        // Act
        Response<UserDto> response = userService.getUserData(1L);

        // Assert
        assertUserResponse(response, STATUS_OK, "User found", userDto);
    }



    private void testChangeTotalSessions(int initialSessions, int change, int expectedSessions) {
        // Arrange
        User user = new User();
        user.setTotalSessions(initialSessions);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(tokenService.isTokenValid(any())).thenReturn(true);
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.changeTotalSessions("valid_jwt", 1L, change);

        // Assert
        assertUserResponse(response, STATUS_OK, "Total sessions changed", true);
        assertThat(user.getTotalSessions()).isEqualTo(expectedSessions);
    }

    @Test
    public void change_total_sessions_increment_test() {
        testChangeTotalSessions(0, 1, 1);
    }

    @Test
    public void change_total_sessions_decrement_test() {
        testChangeTotalSessions(1, -1, 0);
    }

    @Test
    public void change_total_sessions_from_negative_to_positive_test() {
        testChangeTotalSessions(-1, 2, 1);
    }

    @Test
    public void change_total_sessions_from_positive_to_negative_test() {
        testChangeTotalSessions(1, -2, -1);
    }

    @Test
    public void change_total_sessions_zero_change_test() {
        testChangeTotalSessions(1, 0, 1);
    }

    @Test
    public void ban_user_not_found_test() {
        // Arrange
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.empty());
        when(tokenService.isTokenValid(any())).thenReturn(true);
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
        when(tokenService.isTokenValid(any())).thenReturn(true);
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
        when(tokenService.isTokenValid(any())).thenReturn(true);
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
        when(tokenService.isTokenValid(any())).thenReturn(true);
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
        when(tokenService.isTokenValid(any())).thenReturn(true);
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
        when(tokenService.isTokenValid(any())).thenReturn(true);
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
        when(tokenService.isTokenValid(any())).thenReturn(false);
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.banUser("invalid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Invalid or expired token", false);
    }

    @Test
    public void unban_user_invalid_jwt_test() {
        // Arrange
        when(tokenService.isTokenValid(any())).thenReturn(false);
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.unbanUser("invalid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Invalid or expired token", false);
    }

    @Test
    public void ban_user_unauthorized_test() {
        // Arrange
        when(tokenService.isTokenValid(any())).thenReturn(true);
        when(tokenService.getRole(any())).thenReturn("USER");

        // Act
        Response<Boolean> response = userService.banUser("valid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Unauthorized", false);
    }

    @Test
    public void unban_user_unauthorized_test() {
        // Arrange
        when(tokenService.isTokenValid(any())).thenReturn(true);
        when(tokenService.getRole(any())).thenReturn("USER");

        // Act
        Response<Boolean> response = userService.unbanUser("valid_jwt", "username");

        // Assert
        assertUserResponse(response, STATUS_UNAUTHORIZED, "Unauthorized", false);
    }

}
