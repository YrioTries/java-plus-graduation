package ru.practicum.explore_with_me.service.event;

import ru.practicum.explore_with_me.model.dto.event.EventFullDto;
import ru.practicum.explore_with_me.model.dto.event.EventShortDto;
import ru.practicum.explore_with_me.model.dto.event.NewEventDto;
import ru.practicum.explore_with_me.model.dto.event.UpdateEventUserRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getEventsByUser(Long userId, Pageable pageable);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByUser(Long userId, Long eventId);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest);
}