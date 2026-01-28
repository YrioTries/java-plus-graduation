package ru.practicum.explore_with_me.compilation.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.compilation.dao.Compilation;
import ru.practicum.explore_with_me.compilation.mapper.CompilationMapper;
import ru.practicum.explore_with_me.compilation.repository.CompilationRepository;
import ru.practicum.explore_with_me.interaction_api.exception.NotFoundException;
import ru.practicum.explore_with_me.interaction_api.model.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.interaction_api.model.compilation.dto.NewCompilationDto;
import ru.practicum.explore_with_me.interaction_api.model.compilation.dto.UpdateCompilationRequest;
import ru.practicum.explore_with_me.interaction_api.model.event.client.EventServiceClient;
import ru.practicum.explore_with_me.interaction_api.model.event.dto.EventShortDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    private final EventServiceClient eventServiceClient;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);

        Set<EventShortDto> foundEvents = new HashSet<>();
        Set<Long> eventIdsToSave = new HashSet<>();

        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            try {
                foundEvents = eventServiceClient.getEventShortDtoSetByIds(newCompilationDto.getEvents());

                eventIdsToSave = foundEvents.stream()
                        .map(EventShortDto::getId)
                        .collect(Collectors.toSet());

                if (eventIdsToSave.size() != newCompilationDto.getEvents().size()) {
                    Set<Long> missingIds = new HashSet<>(newCompilationDto.getEvents());
                    missingIds.removeAll(eventIdsToSave);
                    log.warn("Some events not found: {}", missingIds);
                    throw new NotFoundException("Некоторые события не найдены: " + missingIds);
                }

            } catch (FeignException e) {
                log.error("Error calling event-service: {}", e.getMessage());

                // Уточните тип исключения
                if (e.status() == 404) {
                    throw new NotFoundException("События не найдены");
                }
                throw new RuntimeException("Error communicating with event-service");
            }
        }

        compilation.setEventsId(eventIdsToSave);
        Compilation savedCompilation = compilationRepository.save(compilation);

        CompilationDto compilationDto = compilationMapper.toCompilationDto(savedCompilation);

        if (!foundEvents.isEmpty()) {
            compilationDto.setEvents(foundEvents);
        } else if (!eventIdsToSave.isEmpty()) {
            try {
                Set<EventShortDto> eventDtos = eventServiceClient.getEventShortDtoSetByIds(eventIdsToSave);
                compilationDto.setEvents(eventDtos);
            } catch (Exception e) {
                log.warn("Could not fetch events for response");
                compilationDto.setEvents(Set.of());
            }
        }

        return compilationDto;
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        if (updateRequest.getEvents() != null) {
            if (!updateRequest.getEvents().isEmpty()) {
                Set<EventShortDto> events = eventServiceClient.getEventShortDtoSetByIds(updateRequest.getEvents());
                if (events.size() != updateRequest.getEvents().size()) {
                    throw new NotFoundException("Некоторые события не найдены");
                }
            }
            compilation.setEventsId(updateRequest.getEvents());
        }

        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);

        CompilationDto compilationDto = compilationMapper.toCompilationDto(updatedCompilation);
        compilationDto.setEvents(eventServiceClient.getEventShortDtoSetByIds(updatedCompilation.getEventsId()));
        return compilationDto;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        if (pinned != null) {
            return compilationRepository.findByPinned(pinned, pageable)
                    .map(compilationMapper::toCompilationDto)
                    .getContent();
        } else {
            return compilationRepository.findAll(pageable)
                    .map(compilationMapper::toCompilationDto)
                    .getContent();
        }
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));
        return compilationMapper.toCompilationDto(compilation);
    }
}