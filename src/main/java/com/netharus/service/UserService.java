package com.netharus.service;

import com.netharus.domain.User;
import com.netharus.domain.dto.request.UserDto;
import com.netharus.domain.dto.response.PageContainer;
import com.netharus.domain.dto.response.UserResponseDto;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User findByUsername(String username);

    User createAccount(UserDto userDto);

    User findById(Long userId);

    void updatePassword(User byUsername, String password);

    PageContainer<UserResponseDto> getPageContainer(Pageable pageable, String keyword);

    void updateStatus(Long userId);
}
