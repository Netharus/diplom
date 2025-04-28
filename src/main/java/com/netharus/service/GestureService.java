package com.netharus.service;

import com.netharus.domain.User;

public interface GestureService {
    void sendGesture(User user, Integer gestureId);
}
