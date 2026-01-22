package ru.practicum.explore_with_me.event.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.explore_with_me.event.dao.Event;
import ru.practicum.explore_with_me.event.dao.Location;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.EventFullDto;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.EventShortDto;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.LocationDto;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.NewEventDto;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "views", ignore = true)
    Event toEvent(NewEventDto newEventDto);

    @Mapping(target = "category")
    @Mapping(target = "initiator")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category")
    @Mapping(target = "initiator")
    EventShortDto toEventShortDto(Event event);

    default Location toLocation(LocationDto dto) {
        if (dto == null) {
            return null;
        }
        Location location = new Location();
        location.setLat(dto.getLat());
        location.setLon(dto.getLon());
        return location;
    }

    @AfterMapping
    default void setDefaultValues(@MappingTarget Event event, NewEventDto newEventDto) {
        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
    }

    default EventFullDto toEventFullDtoWithDetails(Event event, CategoryMapper categoryMapper, UserMapper userMapper) {
        EventFullDto dto = toEventFullDto(event);
        if (event.getCategory_id() != null) {
            dto.setCategory(categoryMapper.toCategoryDto(event.getCategory_id()));
        }
        if (event.getInitiator_id() != null) {
            dto.setInitiator(userMapper.toUserShortDto(event.getInitiator_id()));
        }
        return dto;
    }

    default EventShortDto toEventShortDtoWithDetails(Event event, CategoryMapper categoryMapper, UserMapper userMapper) {
        EventShortDto dto = toEventShortDto(event);
        if (event.getCategory_id() != null) {
            dto.setCategory(categoryMapper.toCategoryDto(event.getCategory_id()));
        }
        if (event.getInitiator_id() != null) {
            dto.setInitiator(userMapper.toUserShortDto(event.getInitiator_id()));
        }
        return dto;
    }
}