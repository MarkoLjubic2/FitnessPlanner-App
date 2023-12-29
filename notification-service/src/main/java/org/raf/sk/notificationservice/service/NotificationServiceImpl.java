package org.raf.sk.notificationservice.service;

import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.dto.ActivationDto;
import org.raf.sk.notificationservice.dto.AppointmentReservationDto;
import org.raf.sk.notificationservice.dto.ChangePasswordDto;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.raf.sk.notificationservice.repository.NotificationRepository;
import org.raf.sk.notificationservice.security.tokenService.TokenService;
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

    private final NotificationRepository notificationRepository;
    private final JavaMailSender emailSender;
    private final TokenService tokenService;

    @Override
    public <T extends NotificationDto> Response<Boolean> sendNotification(T dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("vjukic9222rn@raf.rs");

        if (dto instanceof ActivationDto activationDto) {
            message.setSubject("Account Activation");
            message.setText("Dear " + activationDto.getUsername() + ",\n\nPlease click the following link to activate your account: \n"
                    + "http://localhost:8080/user/activate?token=" + activationDto.getToken());
        }
        else if (dto instanceof AppointmentReservationDto reservationDto) {
            message.setSubject("Training Reservation");
            message.setText("Dear " + reservationDto.getClientFirstName() + " " + reservationDto.getClientLastName() + "," +
                    "\n\nYour reservation for training in hall " + reservationDto.getHallName() + " has been confirmed.\n Price: " + reservationDto.getPrice() + " RSD");
        }
        else if (dto instanceof ChangePasswordDto changePasswordDto) {
            message.setSubject("Password Change");
            message.setText("Dear " + changePasswordDto.getUsername() + ",\n\nYour password has been changed successfully.");
        }
        else {
            throw new IllegalArgumentException("Unknown DTO type: " + dto.getClass().getName());
        }

        try {
            emailSender.send(message);
            return new Response<>(200, "Email sent successfully", true);
        } catch (MailException e) {
            return new Response<>(500, "Failed to send email", false);
        }
    }

    @Override
    public Response<Boolean> deleteNotification(String jwt, Long id) {
        try {
            if (!"ADMIN".equals(tokenService.getUserRole(jwt)))
                return new Response<>(401, "Permission Denied", false);

            notificationRepository.deleteById(id);
            return new Response<>(200, "Notification deleted successfully", true);

        } catch (Exception e) {
            return new Response<>(500, "Failed to delete notification", false);
        }
    }

    @Override
    public <T extends NotificationDto> Response<Page<T>> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <T extends NotificationDto> Response<Page<T>> findAllByUser(String jwt) {
        return null;
    }

    @Override
    public Response<? extends NotificationDto> getNotificationById(Long id) {
        return null;
    }
}
