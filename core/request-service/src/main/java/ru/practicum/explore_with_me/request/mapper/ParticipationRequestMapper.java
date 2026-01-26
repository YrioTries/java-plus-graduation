package ru.practicum.explore_with_me.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore_with_me.interaction_api.model.request.dto.ParticipationRequestDto;
import ru.practicum.explore_with_me.request.dao.ParticipationRequest;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest);
}