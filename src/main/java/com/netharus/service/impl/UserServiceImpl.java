package com.netharus.service.impl;

import com.netharus.domain.User;
import com.netharus.domain.dto.request.UserDto;
import com.netharus.exceptions.AlreadyExistsException;
import com.netharus.exceptions.UserNotFoundException;
import com.netharus.mapper.UserMapper;
import com.netharus.repositories.UserRepository;
import com.netharus.service.UserService;
import com.netharus.stringConstants.ErrorMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(ErrorMessages.USER_NOT_FOUND, userId)));
    }

    @Transactional(readOnly = true)
    protected boolean isExist(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    protected boolean isUserUnique(String username, Long id) {
        return userRepository.findByUsername(username)
                .map(User::getId)
                .filter(userId -> userId.equals(id))
                .isPresent();
    }
}
