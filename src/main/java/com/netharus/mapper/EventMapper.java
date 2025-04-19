package com.netharus.mapper;

import com.netharus.domain.Event;
import com.netharus.domain.dto.response.EventDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> events);
}
