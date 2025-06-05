package com.netharus.service.impl;

import com.netharus.domain.User;
import com.netharus.domain.dto.response.ArduinoResponseDto;
import com.netharus.feignClient.ArduinoClient;
import com.netharus.service.EmergencyStopService;
import com.netharus.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyStopImpl implements EmergencyStopService {

    private final ArduinoClient arduinoClient;
    private final LogService logService;

    @Override
    public ArduinoResponseDto activateEmergencyStop(User user) {
        log.warn("Пользователь {} активирует экстренную остановку", user.getId());
        ArduinoResponseDto response = arduinoClient.emergencyStop();
        logService.success(user, "Экстренная остановка активирована");
        return response;
    }

    @Override
    public ArduinoResponseDto deactivateEmergencyStop(User user) {
        log.info("Пользователь {} деактивирует экстренную остановку", user.getId());
        ArduinoResponseDto response = arduinoClient.emergencyResume();
        logService.success(user, "Экстренная остановка снята");
        return response;
    }
}
