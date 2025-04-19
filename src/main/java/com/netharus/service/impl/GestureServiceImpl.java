package com.netharus.service.impl;

import com.netharus.domain.enums.Gestures;
import com.netharus.exceptions.IllegalScenarioFileFormatException;
import com.netharus.service.GestureService;
import com.netharus.stringConstants.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GestureServiceImpl implements GestureService {


    @Override
    public String sendGesture(int gestureId) {
        if (!Gestures.isGesture(gestureId)) {
            throw new IllegalScenarioFileFormatException(ErrorMessages.ILLEGAL_GESTURE_ID);
        }
        log.info("Отправлен жест с id: {}", gestureId);
        return Gestures.getGestureTitle(gestureId);
    }
}
