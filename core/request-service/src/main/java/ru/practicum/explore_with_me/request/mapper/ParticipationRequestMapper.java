package ru.practicum.explore_with_me.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.interaction_api.model.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.dao.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    @Mapping(target = "event", source = "eventId")      // eventId → event
    @Mapping(target = "requester", source = "requesterId") // requesterId → requester
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);
}