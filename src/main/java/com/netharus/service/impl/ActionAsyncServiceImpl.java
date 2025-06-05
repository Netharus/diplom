package com.netharus.service.impl;

import com.netharus.domain.User;
import com.netharus.domain.dto.request.GestureArduinoDto;
import com.netharus.domain.dto.request.ScenarioArduinoDto;
import com.netharus.domain.dto.response.ArduinoResponseDto;
import com.netharus.domain.dto.response.NotificationDto;
import com.netharus.domain.enums.Gestures;
import com.netharus.domain.enums.Status;
import com.netharus.exceptions.BadRequestException;
import com.netharus.exceptions.NotFoundException;
import com.netharus.feignClient.ArduinoClient;
import com.netharus.lock.GlobalLock;
import com.netharus.service.ActionAsyncService;
import com.netharus.service.EventService;
import com.netharus.service.LogService;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionAsyncServiceImpl implements ActionAsyncService {

    private final GlobalLock globalLock;
    private final ArduinoClient arduinoClient;
    private final LogService logService;
    private final EventService eventService;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @Override
    public void sendAsyncGesture(User user, Integer gestureId) {
        try {
            log.info("Отправлен жест с id: {}", gestureId);
            ArduinoResponseDto arduinoResponseDto = arduinoClient.single(GestureArduinoDto
                    .builder()
                    .gestureId(gestureId)
                    .userId(user.getId())
                    .build());
            log.info(arduinoResponseDto.message());
            sendNotification(arduinoResponseDto);
            logService.success(user, arduinoResponseDto.message());
            eventService.createEvent(Gestures.getGestureTitle(gestureId), user);
            messagingTemplate.convertAndSend("/topic/recent",
                    eventService.getLastFiveEvents(user.getId()));
        } catch (Exception ex) {
            handleException(ex, user);
        } finally {
            log.info("Блокировка c жеста снята");
            globalLock.unlock();
        }
    }

    @Async
    @Override
    public void sendAsyncScenario(User user, List<Integer> gestureIds) {
        try {
            log.info("Полученные жесты: {}", gestureIds);
            ArduinoResponseDto arduinoResponseDto = arduinoClient.batch(ScenarioArduinoDto
                    .builder()
                    .gestureIds(gestureIds)
                    .userId(user.getId())
                    .build());
            log.info(arduinoResponseDto.message());
            sendNotification(arduinoResponseDto);
            logService.success(user, arduinoResponseDto.message());
            eventService.createEvent(Gestures.getGestures(gestureIds), user);
        } catch (Exception ex) {
            handleException(ex, user);
        } finally {
            log.info("Блокировка со сценария снята");
            globalLock.unlock();
        }
    }

    private void sendNotification(ArduinoResponseDto arduinoResponseDto) {
        messagingTemplate.convertAndSend("/topic/notifications",
                NotificationDto
                        .builder()
                        .message(arduinoResponseDto.message())
                        .status(Status.SUCCESS)
                        .build());
    }

    private void handleException(Throwable ex, User user) {
        switch (ex) {
            case BadRequestException badRequestException -> {
                log.warn("Некорректный запрос: {}", ex.getMessage());
                logService.error(user, String.format("Некорректный запрос: %s", ex.getMessage()));
                sendNotification(String.format("Некорректный запрос: %s", ex.getMessage()));
            }
            case NotFoundException notFoundException -> {
                log.warn("Ресурс не найден: {}", ex.getMessage());
                logService.error(user, String.format("Ресурс не найден: %s", ex.getMessage()));
                sendNotification(String.format("Ресурс не найден: %s", ex.getMessage()));
            }
            case RetryableException retryableException -> {
                log.warn("Не удалось соединиться с Arduino: {}", ex.getMessage());
                logService.error(user, "Не удалось соединиться с Arduino");
                sendNotification("Не удалось соединиться с Arduino");
            }
            default -> {
                log.error("Необработанная ошибка: {}", ex.getMessage());
                logService.error(user, String.format("Необработанная ошибка: %s", ex.getMessage()));
                sendNotification(String.format("Необработанная ошибка: %s", ex.getMessage()));
            }
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
