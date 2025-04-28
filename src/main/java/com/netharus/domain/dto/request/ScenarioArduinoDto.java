package com.netharus.domain.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record ScenarioArduinoDto(
        Long userId,
        List<Integer> gestureIds
) {
}
