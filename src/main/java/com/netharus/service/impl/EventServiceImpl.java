package com.netharus.service.impl;

import com.netharus.domain.Event;
import com.netharus.domain.User;
import com.netharus.domain.dto.response.EventDto;
import com.netharus.domain.dto.response.PageContainer;
import com.netharus.mapper.EventMapper;
import com.netharus.repositories.EventRepository;
import com.netharus.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.netharus.service.UtilClass.getPageContainerFromPage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getLastFiveEvents(Long userId) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("dateTime")));
        return getPage(userId, pageable).getContent().stream().map(eventMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<EventDto> getPageContainer(Pageable pageable, Long userId) {
        Page<EventDto> page = getPage(userId, pageable).map(eventMapper::toDto);

        return getPageContainerFromPage(page, pageable, "");
    }

    @Override
    public void createEvent(String scenarioString, User byUsername) {
        Event event = Event.builder()
                .user(byUsername)
                .gesture(scenarioString)
                .build();
        log.info(String.valueOf(eventRepository.save(event)));
    }

    private Page<Event> getPage(Long userId, Pageable pageable) {
        return eventRepository.findAllByUserId(userId, pageable);
    }
}
