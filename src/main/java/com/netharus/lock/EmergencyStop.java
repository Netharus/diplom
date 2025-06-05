package com.netharus.lock;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class EmergencyStop {
    private final AtomicBoolean flag = new AtomicBoolean(false);

    public boolean isActive() {
        return flag.get();
    }

    public void activate() {
        flag.set(true);
    }

    public void deactivate() {
        flag.set(false);
    }
}
