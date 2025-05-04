package com.netharus.service;


import com.netharus.domain.User;
import com.netharus.domain.dto.response.LogResponseDto;
import com.netharus.domain.dto.response.PageContainer;
import org.springframework.data.domain.Pageable;

public interface LogService {
    void success(User user, String message);

    void error(User user, String message);

    PageContainer<LogResponseDto> getPageContainer(Pageable pageable, String keyword);
}
