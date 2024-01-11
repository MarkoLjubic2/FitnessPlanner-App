package org.raf.sk.notificationservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MessageHelper {

    private final Validator validator;
    private final ObjectMapper objectMapper;

    public <T> T getMessage(Message message, Class<T> clazz) throws RuntimeException, JMSException {
        try {
            String json = ((TextMessage) message).getText();
            T data = objectMapper.readValue(json, clazz);

            Set<ConstraintViolation<T>> violations = validator.validate(data);
            if (violations.isEmpty()) return data;

            printViolationsAndThrowException(violations);
            return null;
        } catch (IOException exception) {
            throw new RuntimeException("Message parsing fails.", exception);
        }
    }

    public String createTextMessage(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Problem with creating text message");
        }
    }

    private <T> void printViolationsAndThrowException(Set<ConstraintViolation<T>> violations) {
        String concatenatedViolations = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        throw new RuntimeException(concatenatedViolations);
    }

}
