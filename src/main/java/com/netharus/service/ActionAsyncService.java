package com.netharus.service;

import java.util.List;

public interface ActionAsyncService {

    void sendAsyncGesture(Long userId, Integer gestureId);

    void sendAsyncScenario(Long userId, List<Integer> gestureIds);
}
