package ru.practicum.explore_with_me.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.interaction_api.model.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.dao.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "requesterId", source = "requesterId")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);
}