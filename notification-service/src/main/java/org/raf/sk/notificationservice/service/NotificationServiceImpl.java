package org.raf.sk.notificationservice.service;

import lombok.AllArgsConstructor;
import org.raf.sk.notificationservice.domain.Notification;
import org.raf.sk.notificationservice.domain.Type;
import org.raf.sk.notificationservice.dto.MailDto;
import org.raf.sk.notificationservice.dto.abstraction.NotificationDto;
import org.raf.sk.notificationservice.mapper.NotificationMapper;
import org.raf.sk.notificationservice.repository.NotificationRepository;
import org.raf.sk.notificationservice.repository.TypeRepository;
import org.raf.sk.notificationservice.security.tokenService.TokenService;
import org.raf.sk.notificationservice.service.factory.MessageBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.raf.sk.notificationservice.constants.Constants.*;

@Service
@Transactional
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final TypeRepository typeRepository;
    private final JavaMailSender emailSender;
    private final TokenService tokenService;

    private final Map<Class<? extends NotificationDto>, MessageBuilder<? extends NotificationDto>> messageCreators;

    @Override
    public <T extends NotificationDto> Response<Boolean> sendNotification(String typeName, T dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("vjukic9222rn@raf.rs");

        Optional<Type> type = typeRepository.findTypeByName(typeName);

        return type.map(t -> {
            MessageBuilder<T> messageCreator = (MessageBuilder<T>) messageCreators.get(dto.getClass());
            String body = messageCreator.build(t, dto);
            message.setSubject(t.getSubject());
            message.setText(body);
            try {
                emailSender.send(message);
                archiveNotification(dto, t, body);
                return new Response<>(STATUS_OK, "Email sent successfully", true);
            }
            catch (MailException e) {
                return new Response<>(STATUS_INTERNAL_SERVER_ERROR, "Failed to send email", false);
            }
        }).orElseGet(() -> new Response<>(STATUS_NOT_FOUND, "Type not found", false));
    }

    @Override
    public Response<Boolean> deleteNotification(String jwt, Long id) {
        if (!tokenService.getRoleFromToken(jwt).equals("ADMIN")) {
            return new Response<>(STATUS_FORBIDDEN, "You are not authorized", false);
        }
        notificationRepository.deleteById(id);
        return new Response<>(STATUS_OK, "Notification deleted successfully", true);
    }

    @Override
    public Response<Page<MailDto>> findAll(String jwt, Pageable pageable) {
        if (!tokenService.getRoleFromToken(jwt).equals("ADMIN")) {
            return new Response<>(STATUS_FORBIDDEN, "You are not authorized", null);
        }
        return new Response<>(STATUS_OK, "All notifications", notificationRepository.findAll(pageable).map(notificationMapper::notificationToMailDto));
    }

    @Override
    public Response<Page<MailDto>> findAllByUser(String jwt, Pageable pageable) {
        String mail = tokenService.getMailFromToken(jwt);
        return new Response<>(STATUS_OK, "All notifications",
                notificationRepository.findAllByMail(mail, pageable).map(notificationMapper::notificationToMailDto)
        );
    }

    private <T extends NotificationDto> void archiveNotification(T dto, Type t, String body) {
        Notification notification = new Notification();
        notification.setMail(dto.getMail());
        notification.setType(t);
        notification.setBody(body);
        notification.setTime(LocalDate.now().toString());
        notificationRepository.save(notification);
    }

}
