package com.netharus.service.impl;

import com.netharus.domain.User;
import com.netharus.domain.dto.request.AdminUserCreateDto;
import com.netharus.domain.dto.request.UserDto;
import com.netharus.domain.dto.response.PageContainer;
import com.netharus.domain.dto.response.UserResponseDto;
import com.netharus.exceptions.AlreadyExistsException;
import com.netharus.exceptions.UserNotFoundException;
import com.netharus.mapper.UserMapper;
import com.netharus.repositories.UserRepository;
import com.netharus.service.UserService;
import com.netharus.stringConstants.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.netharus.service.UtilClass.getPageContainerFromPage;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND, username)));
    }

    @Override
    @Transactional
    public User createAccount(UserDto userDto) {
        if (isExist(userDto.username())) {
            throw new AlreadyExistsException(ErrorMessages.USER_ALREADY_EXIST);
        }
        User user = userMapper.fromUserDto(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        log.info("Creating user: {}", user.getUsername());

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND, userId)));
    }

    @Override
    @Transactional
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<UserResponseDto> getPageContainer(Pageable pageable, String keyword) {
        Page<UserResponseDto> usersPage = userRepository.findAll(pageable, keyword)
                .map(userMapper::toUserResponseDto);
        return getPageContainerFromPage(usersPage, pageable, keyword);
    }

    @Override
    @Transactional
    public void updateStatus(Long userId) {
        User user = findById(userId);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createUser(AdminUserCreateDto adminUserCreateDto) {
        if (isExist(adminUserCreateDto.username())) {
            throw new AlreadyExistsException(ErrorMessages.USER_ALREADY_EXIST);
        }
        User user = userMapper.fromAdminUserCreateDto(adminUserCreateDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        log.info("Создан пользователь администратором: {}", user.getUsername());

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isUserUnique(String username, Long id) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .filter(userId -> userId.equals(id))
                .isPresent();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.delete(findById(userId));
    }
}
