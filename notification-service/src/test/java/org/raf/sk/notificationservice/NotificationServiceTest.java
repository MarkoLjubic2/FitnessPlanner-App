package org.raf.sk.notificationservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raf.sk.notificationservice.domain.Notification;
import org.raf.sk.notificationservice.domain.Type;
import org.raf.sk.notificationservice.dto.ActivationDto;
import org.raf.sk.notificationservice.dto.MailDto;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.raf.sk.notificationservice.mapper.NotificationMapper;
import org.raf.sk.notificationservice.repository.NotificationRepository;
import org.raf.sk.notificationservice.repository.TypeRepository;
import org.raf.sk.notificationservice.security.tokenService.TokenService;
import org.raf.sk.notificationservice.service.NotificationServiceImpl;
import org.raf.sk.notificationservice.service.Response;
import org.raf.sk.notificationservice.service.factory.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.raf.sk.notificationservice.constants.Constants.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationServiceImpl notificationService;
    @MockBean
    private NotificationRepository notificationRepository;
    @MockBean
    private NotificationMapper notificationMapper;
    @MockBean
    private TypeRepository typeRepository;
    @MockBean
    private JavaMailSender emailSender;
    @MockBean
    private TokenService tokenService;
    @Autowired
    private Map<Class<? extends NotificationDto>, MessageBuilder<? extends NotificationDto>> messageCreators;

    // Helper
    private <U, V> void assertNotificationResponse(Response<U> response, int expectedStatusCode, String expectedMessage, V expectedData) {
        assertThat(response.getStatusCode()).isEqualTo(expectedStatusCode);
        assertThat(response.getMessage()).isEqualTo(expectedMessage);
        assertThat(response.getData()).isEqualTo(expectedData);
    }

    @Test
    public void send_notification_null_type_mame() {
        // Arrange
        NotificationDto dto = new ActivationDto();

        // Act
        Response<Boolean> response = notificationService.sendNotification(null, dto);

        // Assert
        assertNotificationResponse(response, STATUS_NOT_FOUND, "Type not found", false);
    }

    @Test
    public void send_notification_null_dto() {
        // Arrange
        String typeName = "ACTIVATION";

        // Act
        Response<Boolean> response = notificationService.sendNotification(typeName, null);

        // Assert
        assertNotificationResponse(response, STATUS_NOT_FOUND, "Type not found", false);
    }

    @Test
    public void send_notification_invalid_type() {
        // Arrange
        String typeName = "INVALID";
        NotificationDto dto = new ActivationDto();

        // Act
        Response<Boolean> response = notificationService.sendNotification(typeName, dto);

        // Assert
        assertNotificationResponse(response, STATUS_NOT_FOUND, "Type not found", false);
    }

    @Test
    public void send_notification_valid_type() {
        // Arrange
        String typeName = "ACTIVATION";
        ActivationDto dto = new ActivationDto();
        dto.setUsername("NAME");
        dto.setMail("test@gmail.com");
        Type type = new Type();
        type.setName(typeName);
        type.setTemplate("Greetings %USERNAME! Please click the following link to activate your account: %LINK");

        when(typeRepository.findTypeByName(anyString())).thenReturn(Optional.of(type));
        doNothing().when(emailSender).send(any(SimpleMailMessage.class));

        // Act
        Response<Boolean> response = notificationService.sendNotification(typeName, dto);

        // Assert
        assertNotificationResponse(response, STATUS_OK, "Email sent successfully", true);
        verify(typeRepository, times(1)).findTypeByName(anyString());
        verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void delete_notification_not_admin() {
        // Arrange
        String jwt = "jwt";
        when(tokenService.getRoleFromToken(anyString())).thenReturn("USER");

        // Act
        Response<Boolean> response = notificationService.deleteNotification(jwt, 1L);

        // Assert
        assertNotificationResponse(response, STATUS_FORBIDDEN, "You are not authorized", false);
    }

    @Test
    public void deleteNotification_admin() {
        // Arrange
        String jwt = "jwt";
        when(tokenService.getRoleFromToken(anyString())).thenReturn("ADMIN");

        // Act
        Response<Boolean> response = notificationService.deleteNotification(jwt, 1L);

        // Assert
        assertNotificationResponse(response, STATUS_OK, "Notification deleted successfully", true);
    }

    @Test
    public void find_all_notifications_not_admin() {
        // Arrange
        String jwt = "jwt";
        Pageable pageable = PageRequest.of(0, 10);
        when(tokenService.getRoleFromToken(anyString())).thenReturn("USER");

        // Act
        Response<Page<MailDto>> response = notificationService.findAll(jwt, pageable);

        // Assert
        assertNotificationResponse(response, STATUS_FORBIDDEN, "You are not authorized", null);
    }

    @Test
    public void find_all_notifications_admin() {
        // Arrange
        String jwt = "jwt";
        Pageable pageable = PageRequest.of(0, 10);
        when(tokenService.getRoleFromToken(anyString())).thenReturn("ADMIN");

        Page<Notification> notificationPage = new PageImpl<>(List.of(new Notification()));
        Page<MailDto> expectedPage = notificationPage.map(notificationMapper::notificationToMailDto);
        when(notificationRepository.findAll(any(Pageable.class))).thenReturn(notificationPage);

        // Act
        Response<Page<MailDto>> response = notificationService.findAll(jwt, pageable);

        // Assert
        assertNotificationResponse(response, STATUS_OK, "All notifications", expectedPage);
    }

    @Test
    public void find_all_notifications_by_user() {
        // Arrange
        String jwt = "jwt";
        Pageable pageable = PageRequest.of(0, 10);
        String mail = "test@gmail.com";
        when(tokenService.getMailFromToken(anyString())).thenReturn(mail);

        Page<Notification> notificationPage = new PageImpl<>(List.of(new Notification()));
        Page<MailDto> expectedPage = notificationPage.map(notificationMapper::notificationToMailDto);
        when(notificationRepository.findAllByMail(anyString(), any(Pageable.class))).thenReturn(notificationPage);

        // Act
        Response<Page<MailDto>> response = notificationService.findAllByUser(jwt, pageable);

        // Assert
        assertNotificationResponse(response, STATUS_OK, "All notifications", expectedPage);
    }


}