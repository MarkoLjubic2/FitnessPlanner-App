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
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
                return new Response<>(200, "Email sent successfully", true);
            }
            catch (MailException e) {
                return new Response<>(500, "Failed to send email", false);
            }
        }).orElseGet(() -> new Response<>(404, "Type not found", false));
    }

    @Override
    public Response<Boolean> deleteNotification(Long id) {
        notificationRepository.deleteById(id);
        return new Response<>(200, "Notification deleted successfully", true);
    }

    @Override
    public Response<Page<MailDto>> findAll(Pageable pageable) {
        return new Response<>(200, "All notifications", notificationRepository.findAll(pageable).map(notificationMapper::notificationToMailDto));
    }

    @Override
    public Response<Page<MailDto>> findAllByUser(Pageable pageable, String jwt) {
        String mail = tokenService.getMailFromToken(jwt);
        return new Response<>(200, "All notifications",
                notificationRepository.findAllByMail(mail, pageable).map(notificationMapper::notificationToMailDto)
        );
    }

    private <T extends NotificationDto> void archiveNotification(T dto, Type t, String body) {
        Notification notification = new Notification();
        notification.setMail(dto.getMail());
        notification.setType(t);
        notification.setBody(body);
        notification.setTime(new Date());
        notificationRepository.save(notification);
    }

}
