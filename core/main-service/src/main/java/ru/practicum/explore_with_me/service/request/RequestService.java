package ru.practicum.explore_with_me.service.request;

import ru.practicum.explore_with_me.model.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explore_with_me.model.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explore_with_me.model.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}