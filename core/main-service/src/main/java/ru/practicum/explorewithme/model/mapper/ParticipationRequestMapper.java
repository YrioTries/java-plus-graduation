package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.model.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.model.dao.Event;
import ru.practicum.explorewithme.model.dao.ParticipationRequest;
import ru.practicum.explorewithme.model.dao.User;

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