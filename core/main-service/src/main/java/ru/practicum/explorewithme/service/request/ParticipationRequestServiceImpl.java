package ru.practicum.explorewithme.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.model.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.model.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.model.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.model.enums.EventState;
import ru.practicum.explorewithme.model.enums.RequestStatus;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.model.mapper.ParticipationRequestMapper;
import ru.practicum.explorewithme.model.dao.EventDao;
import ru.practicum.explorewithme.model.dao.ParticipationRequestDao;
import ru.practicum.explorewithme.model.dao.UserDao;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements RequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        EventDao event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " not found for user with id=" + userId);
        }

        return participationRequestRepository.findByEventId(eventId).stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        EventDao event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " not found for user with id=" + userId);
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Event does not require request moderation");
        }

        List<Long> requestIds = new ArrayList<>(updateRequest.getRequestIds());
        if (requestIds.isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        Map<Long, ParticipationRequestDao> requestsMap = participationRequestRepository.findByIdIn(requestIds)
                .stream()
                .collect(Collectors.toMap(ParticipationRequestDao::getId, Function.identity()));

        validateAllRequestsFound(requestIds, requestsMap);

        validateRequestsMap(requestsMap, eventId);

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        RequestStatus status = updateRequest.getStatus();
        if (status == RequestStatus.CONFIRMED) {
            processConfirmationWithMap(event, requestsMap, requestIds, confirmedRequests, rejectedRequests);
        } else if (status == RequestStatus.REJECTED) {
            processRejectionWithMap(requestsMap, requestIds, rejectedRequests);
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private void validateAllRequestsFound(List<Long> requestIds, Map<Long, ParticipationRequestDao> requestsMap) {
        if (requestsMap.size() != requestIds.size()) {
            List<Long> foundIds = new ArrayList<>(requestsMap.keySet());
            List<Long> missingIds = requestIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            throw new NotFoundException("Requests not found with ids: " + missingIds);
        }
    }

    private void validateRequestsMap(Map<Long, ParticipationRequestDao> requestsMap, Long eventId) {
        List<String> errors = new ArrayList<>();

        for (ParticipationRequestDao request : requestsMap.values()) {
            if (!request.getEvent().getId().equals(eventId)) {
                errors.add("Request with id=" + request.getId() + " does not belong to event with id=" + eventId);
            }
            if (request.getStatus() != RequestStatus.PENDING) {
                errors.add("Request with id=" + request.getId() + " must be in PENDING state");
            }
        }

        if (!errors.isEmpty()) {
            throw new ConflictException(String.join("; ", errors));
        }
    }

    private void processConfirmationWithMap(EventDao event, Map<Long, ParticipationRequestDao> requestsMap,
                                            List<Long> requestIds, List<ParticipationRequestDto> confirmedRequests,
                                            List<ParticipationRequestDto> rejectedRequests) {
        Long confirmedCount = participationRequestRepository.countConfirmedRequestsByEventId(event.getId());
        int participantLimit = event.getParticipantLimit();
        int availableSlots = participantLimit - confirmedCount.intValue();

        List<ParticipationRequestDao> requestsToUpdate = new ArrayList<>();
        List<ParticipationRequestDao> requestsToReject = new ArrayList<>();

        for (int i = 0; i < requestIds.size(); i++) {
            Long requestId = requestIds.get(i);
            ParticipationRequestDao request = requestsMap.get(requestId);

            if (i < availableSlots) {
                request.setStatus(RequestStatus.CONFIRMED);
                requestsToUpdate.add(request);
            } else {
                request.setStatus(RequestStatus.REJECTED);
                requestsToReject.add(request);
            }
        }

        saveAndMapResults(requestsToUpdate, requestsToReject, confirmedRequests, rejectedRequests);

        if (availableSlots <= requestsToUpdate.size()) {
            rejectAllPendingRequests(event.getId(), rejectedRequests);
        }
    }

    private void processRejectionWithMap(Map<Long, ParticipationRequestDao> requestsMap,
                                         List<Long> requestIds, List<ParticipationRequestDto> rejectedRequests) {
        List<ParticipationRequestDao> requestsToReject = requestIds.stream()
                .map(requestsMap::get)
                .collect(Collectors.toList());

        requestsToReject.forEach(request -> request.setStatus(RequestStatus.REJECTED));
        List<ParticipationRequestDao> savedRequests = participationRequestRepository.saveAll(requestsToReject);

        rejectedRequests.addAll(savedRequests.stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList()));
    }

    private void saveAndMapResults(List<ParticipationRequestDao> requestsToUpdate,
                                   List<ParticipationRequestDao> requestsToReject,
                                   List<ParticipationRequestDto> confirmedRequests,
                                   List<ParticipationRequestDto> rejectedRequests) {
        if (!requestsToUpdate.isEmpty()) {
            List<ParticipationRequestDao> savedConfirmed = participationRequestRepository.saveAll(requestsToUpdate);
            confirmedRequests.addAll(savedConfirmed.stream()
                    .map(participationRequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        }

        if (!requestsToReject.isEmpty()) {
            List<ParticipationRequestDao> savedRejected = participationRequestRepository.saveAll(requestsToReject);
            rejectedRequests.addAll(savedRejected.stream()
                    .map(participationRequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        }
    }

    private void rejectAllPendingRequests(Long eventId, List<ParticipationRequestDto> rejectedRequests) {
        List<ParticipationRequestDao> pendingRequests = participationRequestRepository
                .findByEventIdAndStatus(eventId, RequestStatus.PENDING);

        if (!pendingRequests.isEmpty()) {
            Map<Long, ParticipationRequestDao> pendingMap = pendingRequests.stream()
                    .collect(Collectors.toMap(ParticipationRequestDao::getId, Function.identity()));

            pendingMap.values().forEach(request -> request.setStatus(RequestStatus.REJECTED));
            List<ParticipationRequestDao> savedRequests = participationRequestRepository.saveAll(pendingMap.values());

            rejectedRequests.addAll(savedRequests.stream()
                    .map(participationRequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList()));
        }
    }


    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        return participationRequestRepository.findByRequesterId(userId).stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        UserDao user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        EventDao event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        participationRequestRepository.findByEventAndRequester(event, user)
                .ifPresent(existingRequest -> {
                    throw new ConflictException("Request from user with id=" + userId +
                            " to event with id=" + eventId + " already exists");
                });

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in their own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        if (event.getParticipantLimit() > 0) {
            Long confirmedCount = participationRequestRepository.countConfirmedRequestsByEventId(eventId);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Participant limit reached for event with id=" + eventId);
            }
        }

        ParticipationRequestDao request = new ParticipationRequestDao();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        ParticipationRequestDao savedRequest = participationRequestRepository.save(request);
        return participationRequestMapper.toParticipationRequestDto(savedRequest);
    }


    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        UserDao user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        ParticipationRequestDao request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Request with id=" + requestId +
                    " not found for user with id=" + userId);
        }

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequestDao savedRequest = participationRequestRepository.save(request);
        return participationRequestMapper.toParticipationRequestDto(savedRequest);
    }
}