package com.netharus.service;

import com.netharus.domain.User;
import com.netharus.domain.dto.response.ArduinoResponseDto;

public interface EmergencyStopService {

    ArduinoResponseDto deactivateEmergencyStop(User user);

    ArduinoResponseDto activateEmergencyStop(User user);

}
