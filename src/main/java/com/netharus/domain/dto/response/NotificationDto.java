package com.netharus.domain.dto.response;

import com.netharus.domain.enums.Status;
import lombok.Builder;

@Builder
public record NotificationDto(
        String message,
        Status status
) {
}
