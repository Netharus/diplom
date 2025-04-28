package com.netharus.service.impl;

import com.netharus.domain.dto.request.GestureArduinoDto;
import com.netharus.domain.dto.request.ScenarioArduinoDto;
import com.netharus.domain.dto.response.ArduinoResponseDto;
import com.netharus.feignClient.ArduinoClient;
import com.netharus.lock.GlobalLock;
import com.netharus.service.ActionAsyncService;
import com.netharus.service.EventService;
import com.netharus.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        } finally {
            log.info("Блокировка c жеста снята");
            globalLock.unlock();
        }
    }

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
        } finally {
            log.info("Блокировка со сценария снята");
            globalLock.unlock();
        }
    }
}
