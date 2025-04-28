package com.netharus.service.impl;

import com.netharus.domain.User;
import com.netharus.domain.dto.request.GestureArduinoDto;
import com.netharus.domain.dto.request.ScenarioArduinoDto;
import com.netharus.domain.dto.response.ArduinoResponseDto;
import com.netharus.domain.dto.response.NotificationDto;
import com.netharus.domain.enums.Gestures;
import com.netharus.domain.enums.Status;
import com.netharus.feignClient.ArduinoClient;
import com.netharus.lock.GlobalLock;
import com.netharus.service.ActionAsyncService;
import com.netharus.service.EventService;
import com.netharus.service.LogService;
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
}
