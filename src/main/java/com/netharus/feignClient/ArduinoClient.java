package com.netharus.feignClient;

import com.netharus.domain.dto.request.GestureArduinoDto;
import com.netharus.domain.dto.request.ScenarioArduinoDto;
import com.netharus.domain.dto.response.ArduinoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "arduinoClient")
public interface ArduinoClient {

    @PostMapping("/single")
    ArduinoResponseDto single(@RequestBody GestureArduinoDto arduinoDto);

    @PostMapping("/batch")
    ArduinoResponseDto batch(@RequestBody ScenarioArduinoDto scenarioArduinoDto);

    @GetMapping("/test")
    ArduinoResponseDto test();
}
