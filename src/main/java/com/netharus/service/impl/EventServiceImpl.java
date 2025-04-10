package com.netharus.service.impl;

import com.netharus.domain.Event;
import com.netharus.repositories.EventRepository;
import com.netharus.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<Event> getLastFiveEvents(Long userId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("dateTime")));
        return eventRepository.findAll(pageable).getContent();
    }
}
