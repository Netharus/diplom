package com.netharus.controller.admin.rest;

import com.netharus.domain.dto.request.AdminUserCreateDto;
import com.netharus.domain.dto.request.AdminUserUpdateDto;
import com.netharus.domain.dto.response.ErrorResponseDto;
import com.netharus.domain.dto.response.PageContainer;
import com.netharus.domain.dto.response.UserResponseDto;
import com.netharus.exceptions.AlreadyExistsException;
import com.netharus.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@Slf4j
@PreAuthorize("hasAuthority('ADMIN')")
public class RestAdminUserController {

    private final UserService userService;

    @PatchMapping("/changeStatus/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public String changeStatus(@PathVariable Long userId) {
        userService.updateStatus(userId);
        return String.format("Статус пользователя с id: %d обновлен", userId);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public PageContainer<UserResponseDto> users(@PageableDefault(sort = "active") Pageable pageable,
                                                @RequestParam(defaultValue = "") String keyword) {
        return userService.getPageContainer(pageable, keyword);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    public String createUser(@RequestBody AdminUserCreateDto adminUserCreateDto) {
        userService.createUser(adminUserCreateDto);
        return "Пользователь создан";
    }

    @GetMapping("/isExistUsernameCreate")
    public boolean isExistUsername(@RequestParam String username) {
        return userService.isExist(username);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public String updateUser(@RequestBody AdminUserUpdateDto adminUserUpdateDto) {
        userService.updateUser(adminUserUpdateDto);
        return "Пользователь успешно обновлен";
    }

    @ExceptionHandler({AlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto alreadyExistsExceptionHandler(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponseDto(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }
}
