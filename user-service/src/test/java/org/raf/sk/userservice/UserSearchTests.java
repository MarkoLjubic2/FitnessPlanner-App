package org.raf.sk.userservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raf.sk.userservice.domain.User;
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
public class UserSearchTests {

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

}
