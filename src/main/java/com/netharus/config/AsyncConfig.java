package com.netharus.config;

import com.netharus.exceptions.handler.CustomAsyncExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.AsyncConfigurer;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler(messagingTemplate);
    }
}

