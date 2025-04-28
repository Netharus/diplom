package com.netharus.service;


import com.netharus.domain.User;

public interface LogService {
    void success(User user, String message);

    void error(User user, String message);
}
