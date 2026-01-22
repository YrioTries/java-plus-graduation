package ru.practicum.explore_with_me.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.interaction_api.model.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.dao.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);

    default Long mapEventToLong(Event event) {
        return event != null ? event.getId() : null;
    }

    default Long mapUserToLong(User user) {
        return user != null ? user.getId() : null;
    }
}