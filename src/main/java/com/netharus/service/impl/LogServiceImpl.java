package com.netharus.service.impl;

import com.netharus.domain.Log;
import com.netharus.domain.User;
import com.netharus.domain.enums.Status;
import com.netharus.repositories.LogRepository;
import com.netharus.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;

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
}
