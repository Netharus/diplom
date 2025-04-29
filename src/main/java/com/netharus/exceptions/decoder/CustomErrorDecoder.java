package com.netharus.exceptions.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netharus.domain.dto.response.ArduinoResponseDto;
import com.netharus.exceptions.BadRequestException;
import com.netharus.exceptions.NotFoundException;
import com.netharus.service.LogService;
import com.netharus.service.UserService;
import com.netharus.stringConstants.ErrorMessages;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LogService logService;
    private final UserService userService;

    @Override
    public Exception decode(String methodKey, Response response) {
        ArduinoResponseDto errorResponse = parseError(response);
        logService.error(userService.findById(errorResponse.userId()), errorResponse.message());
        return switch (response.status()) {
            case 400 -> new BadRequestException(errorResponse.message());
            case 404 -> new NotFoundException(errorResponse.message());
            default -> new RuntimeException(ErrorMessages.UNEXPECTED_ERROR + errorResponse.message());
        };
    }

    private ArduinoResponseDto parseError(Response response) {
        try (InputStream body = response.body().asInputStream()) {
            return objectMapper.readValue(body, ArduinoResponseDto.class);
        } catch (IOException e) {
            return new ArduinoResponseDto(ErrorMessages.PARSE_ERROR);
        }
    }
}
