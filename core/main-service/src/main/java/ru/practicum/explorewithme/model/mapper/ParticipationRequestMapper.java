package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.model.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.model.dao.EventDao;
import ru.practicum.explorewithme.model.dao.ParticipationRequestDao;
import ru.practicum.explorewithme.model.dao.UserDao;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toParticipationRequestDto(ParticipationRequestDao participationRequest);

    default Long mapEventToLong(EventDao event) {
        return event != null ? event.getId() : null;
    }

    default Long mapUserToLong(UserDao user) {
        return user != null ? user.getId() : null;
    }
}