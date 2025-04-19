package com.netharus.domain.dto.response;

import lombok.Builder;

@Builder
public record EventDto(
        String dateTime,
        String gesture
) {
    public EventDto {
        dateTime = dateTime.replaceAll("T", " ");
    }
}
