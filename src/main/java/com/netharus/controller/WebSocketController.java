package com.netharus.controller;

import com.netharus.domain.dto.response.EventDto;
import com.netharus.domain.dto.response.NotificationDto;
import com.netharus.domain.dto.response.PageContainer;
import com.netharus.domain.dto.response.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
public class WebSocketController {

    @MessageMapping("/notifications")
    @SendTo("/topic/notifications")
    public NotificationDto sendNotification(NotificationDto notificationDto) {
        log.info("Получено уведомление: {}", notificationDto);
        return notificationDto;
    }

    @MessageMapping("/recent")
    @SendTo("/topic/recent")
    public List<EventDto> sendRecentEvents(List<EventDto> eventDtoList) {
        log.info("Получен список последних событий: {}", eventDtoList);
        return eventDtoList;
    }

    @MessageMapping("/users")
    @SendTo("/topic/users")
    public PageContainer<UserResponseDto> sendUsers(PageContainer<UserResponseDto> userResponsePage) {
        return userResponsePage;
    }
}
