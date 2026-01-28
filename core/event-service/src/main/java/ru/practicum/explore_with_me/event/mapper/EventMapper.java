package ru.practicum.explore_with_me.event.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.explore_with_me.event.dao.Event;
import ru.practicum.explore_with_me.event.dao.Location;
import ru.practicum.explore_with_me.interaction_api.model.category.dto.CategoryDto;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.EventFullDto;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.EventShortDto;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.LocationDto;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.NewEventDto;
import ru.practicum.explore_with_me.interaction_api.model.user.dto.UserShortDto;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "views", ignore = true)
    Event toEvent(NewEventDto newEventDto);

    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "initiator.id", source = "initiatorId")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "initiator.id", source = "initiatorId")
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

    default LocationDto toLocationDto(Location location) {
        if (location == null) {
            return null;
        }
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());
        return locationDto;
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

    default EventFullDto toEventFullDtoWithDetails(Event event, CategoryDto categoryDto, UserShortDto userShortDto) {
        EventFullDto dto = toEventFullDto(event);
        if (event.getCategoryId() != null) {
            dto.setCategory(categoryDto);
        }
        if (event.getInitiatorId() != null) {
            dto.setInitiator(userShortDto);
        }
        return dto;
    }

    default EventShortDto toEventShortDtoWithDetails(Event event, CategoryDto categoryDto, UserShortDto userShortDto) {
        EventShortDto dto = toEventShortDto(event);
        if (event.getCategoryId() != null) {
            dto.setCategory(categoryDto);
        }
        if (event.getInitiatorId() != null) {
            dto.setInitiator(userShortDto);
        }
        return dto;
    }
}