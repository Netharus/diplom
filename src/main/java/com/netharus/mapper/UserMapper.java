package com.netharus.mapper;

import com.netharus.domain.User;
import com.netharus.domain.dto.request.UserDto;
import com.netharus.domain.dto.response.UserResponseDto;
import com.netharus.domain.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User fromUserDto(UserDto userDto);

    @Mapping(target = "role", source = "role", qualifiedByName = "role")
    UserResponseDto toUserResponseDto(User user);

    @Named("role")
    default String roleToString(Role role) {
        return role.getDef();
    }
}
