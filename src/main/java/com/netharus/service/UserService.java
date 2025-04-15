package com.netharus.service;

import com.netharus.domain.User;
import com.netharus.domain.dto.request.UserDto;

public interface UserService {
    User findByUsername(String username);

    User createAccount(UserDto userDto);
}
