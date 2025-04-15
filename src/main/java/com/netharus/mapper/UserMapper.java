package com.netharus.mapper;

import com.netharus.domain.User;
import com.netharus.domain.dto.request.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User fromUserDto(UserDto userDto);
}
