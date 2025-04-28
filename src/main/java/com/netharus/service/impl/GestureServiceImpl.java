package com.netharus.service.impl;

import com.netharus.domain.User;
import com.netharus.domain.enums.Gestures;
import com.netharus.exceptions.IllegalScenarioFileFormatException;
import com.netharus.lock.GlobalLock;
import com.netharus.service.ActionAsyncService;
import com.netharus.service.GestureService;
import com.netharus.stringConstants.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestureServiceImpl implements GestureService {

    private final GlobalLock globalLock;
    private final ActionAsyncService actionAsyncService;


    @Override
    public void sendGesture(User user, Integer gestureId) {
        if (!Gestures.isGesture(gestureId)) {
            log.warn("Блокировка c жеста снята из-за ошибки");
            globalLock.unlock();
            throw new IllegalScenarioFileFormatException(String.format(ErrorMessages.ILLEGAL_GESTURE_ID, gestureId));
        }
        actionAsyncService.sendAsyncGesture(user, gestureId);
    }
}
