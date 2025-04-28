package com.netharus.service;

import com.netharus.domain.User;

import java.util.List;

public interface ActionAsyncService {

    void sendAsyncGesture(User user, Integer gestureId);

    void sendAsyncScenario(User user, List<Integer> gestureIds);
}
