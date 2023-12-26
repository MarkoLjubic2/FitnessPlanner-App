package org.raf.sk.notificationservice.service;

import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.dto.ActivationDto;
import org.raf.sk.notificationservice.dto.ChangePasswordDto;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender emailSender;

    @Override
    public <T> Response<Boolean> sendNotification(T dto) {
        if (!(dto instanceof ActivationDto activationDto)) {
            return new Response<>(400, "Invalid DTO type", false);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("testttt");
        message.setSubject("Account Activation");
        message.setText("Dear " + activationDto.getUsername() + ",\n\nPlease click the following link to activate your account: \n"
                + "http://localhost:8080/activate?token=" + activationDto.getToken());

        try {
            emailSender.send(message);
            return new Response<>(200, "Email sent successfully", true);
        } catch (MailException e) {
            return new Response<>(500, "Failed to send email", false);
        }
    }

    @Override
    public Response<Boolean> deleteNotification(Long id) {
        return null;
    }

    @Override
    public <T extends NotificationDto> Response<Page<T>> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Response<? extends NotificationDto> getNotificationById(Long id) {
        return null;
    }
}
