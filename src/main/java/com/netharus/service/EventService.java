package com.netharus.service;

import com.netharus.domain.User;
import com.netharus.domain.dto.response.EventDto;
import com.netharus.domain.dto.response.PageContainer;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventService {
    List<EventDto> getLastFiveEvents(Long userId);

    PageContainer<EventDto> getPageContainer(Pageable pageable, Long userId);

    void createEvent(String scenarioString, User user);
}
