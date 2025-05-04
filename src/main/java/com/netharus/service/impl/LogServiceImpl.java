package com.netharus.service.impl;

import com.netharus.domain.Log;
import com.netharus.domain.User;
import com.netharus.domain.dto.response.LogResponseDto;
import com.netharus.domain.dto.response.PageContainer;
import com.netharus.domain.enums.Status;
import com.netharus.mapper.LogsMapper;
import com.netharus.repositories.LogRepository;
import com.netharus.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.netharus.service.UtilClass.getPageContainerFromPage;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final LogsMapper logsMapper;

    @Override
    @Transactional
    public void success(User user, String message) {
        logRepository.save(Log.builder()
                .status(Status.SUCCESS)
                .message(message)
                .user(user)
                .build());
    }

    @Override
    @Transactional
    public void error(User user, String message) {
        logRepository.save(Log.builder()
                .status(Status.ERROR)
                .message(message)
                .user(user)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<LogResponseDto> getPageContainer(Pageable pageable, String keyword) {
        Page<LogResponseDto> logPage = logRepository.findAll(pageable, keyword).map(logsMapper::toDto);
        return getPageContainerFromPage(logPage, pageable, keyword);
    }
}
