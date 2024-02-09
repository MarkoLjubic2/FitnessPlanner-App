package org.raf.sk.userservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.raf.sk.userservice.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserChangeSessionsTests {

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

    private void testChangeTotalSessions(int initialSessions, int change, int expectedSessions) {
        // Arrange
        User user = new User();
        user.setTotalSessions(initialSessions);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(tokenService.getRole(any())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = userService.changeTotalSessions( 1L, change);

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

}
