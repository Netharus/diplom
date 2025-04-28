package com.netharus.lock;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GlobalLock {
    private boolean lock = false;


    public boolean tryLock() {
        if (lock) {
            return false;
        } else {
            lock = true;
            return true;
        }
    }

    public void unlock() {
        lock = false;
    }
}
