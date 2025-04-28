package com.netharus.mapper;

import com.netharus.domain.Event;
import com.netharus.domain.dto.response.EventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    @Mapping(target = "dateTime", source = "dateTime", dateFormat = "dd.MM.yyyy HH:mm:ss")
    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> events);
}
