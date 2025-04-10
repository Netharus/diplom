package com.netharus.service;

import com.netharus.domain.Event;

import java.util.List;

public interface EventService {
    List<Event> getLastFiveEvents(Long userId);
}
