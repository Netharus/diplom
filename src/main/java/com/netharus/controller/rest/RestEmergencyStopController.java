package com.netharus.controller.rest;

import com.netharus.domain.dto.response.ArduinoResponseDto;
import com.netharus.lock.EmergencyStop;
import com.netharus.service.EmergencyStopService;
import com.netharus.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
@Slf4j
public class RestEmergencyStopController {

    private final EmergencyStop emergencyStop;
    private final EmergencyStopService emergencyStopService;
    private final UserService userService;

    @PostMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    public ArduinoResponseDto activateEmergency(@AuthenticationPrincipal UserDetails userDetails) {
        emergencyStop.activate();
        log.info("Activated emergency stop");
        return emergencyStopService.activateEmergencyStop(userService.findByUsername(userDetails.getUsername()));
    }

    @PostMapping("/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public ArduinoResponseDto deactivateEmergency(@AuthenticationPrincipal UserDetails userDetails) {
        emergencyStop.deactivate();
        log.info("Deactivated emergency stop");
        return emergencyStopService.deactivateEmergencyStop(userService.findByUsername(userDetails.getUsername()));
    }
}
