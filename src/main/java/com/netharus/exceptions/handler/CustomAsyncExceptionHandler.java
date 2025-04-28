package com.netharus.exceptions.handler;

import com.netharus.domain.dto.response.NotificationDto;
import com.netharus.domain.enums.Status;
import com.netharus.exceptions.BadRequestException;
import com.netharus.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("❗ Async exception in method: {}", method.getName());
        if (ex instanceof BadRequestException) {
            log.warn("Некорректный запрос: {}", ex.getMessage());
            sendNotification(String.format("Некорректный запрос: %s", ex.getMessage()));
        } else if (ex instanceof NotFoundException) {
            log.warn("Ресурс не найден: {}", ex.getMessage());
            sendNotification(String.format("Ресурс не найден: %s", ex.getMessage()));
        } else {
            log.error("Необработанная ошибка: {}", ex.getMessage());
            sendNotification(String.format("Необработанная ошибка: %s", ex.getMessage()));
        }
    }

    private void sendNotification(String message) {
        messagingTemplate.convertAndSend("/topic/notifications",
                NotificationDto
                        .builder()
                        .message(message)
                        .status(Status.ERROR)
                        .build());
    }
}

