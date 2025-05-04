package com.netharus.mapper;

import com.netharus.domain.Log;
import com.netharus.domain.dto.response.LogResponseDto;
import com.netharus.domain.enums.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LogsMapper {

    @Mapping(target = "dateTime", source = "dateTime", dateFormat = "dd.MM.yyyy HH:mm:ss")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "status", source = "status", qualifiedByName = "status")
    LogResponseDto toDto(Log log);

    @Named("status")
    default String statusToString(Status status) {
        return status.getTitle();
    }
}
