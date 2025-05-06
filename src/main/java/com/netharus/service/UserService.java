package com.netharus.service;

import com.netharus.domain.User;
import com.netharus.domain.dto.request.AdminUserCreateDto;
import com.netharus.domain.dto.request.AdminUserUpdateDto;
import com.netharus.domain.dto.request.UserDto;
import com.netharus.domain.dto.response.PageContainer;
import com.netharus.domain.dto.response.UserResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    User findByUsername(String username);

    User createAccount(UserDto userDto);

    User findById(Long userId);

    void updatePassword(User byUsername, String password);

    PageContainer<UserResponseDto> getPageContainer(Pageable pageable, String keyword);

    void updateStatus(Long userId);

    void createUser(AdminUserCreateDto adminUserCreateDto);

    boolean isExist(String username);

    boolean isUserUnique(String username, Long id);

    void deleteUser(Long userId);

    void updateUser(AdminUserUpdateDto adminUserUpdateDto);

    void guideViewed(UserDetails userDetails);
}
