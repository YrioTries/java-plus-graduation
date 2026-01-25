package ru.practicum.explore_with_me.compilation.service;

import lombok.RequiredArgsConstructor;
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

        Set<Long> events;
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = new HashSet<>(eventServiceClient
                    .findAllEventsClient(newCompilationDto.getEvents())
                    .stream()
                    .map(EventShortDto::getId)
                    .toList());

        } else {
            events = new HashSet<>();
        }
        compilation.setEventsId(events);

        Compilation savedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(savedCompilation);
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
            Set<Long> events = new HashSet<>(eventServiceClient
                    .findAllEventsClient(updateRequest.getEvents())
                    .stream()
                    .map(EventShortDto::getId)
                    .toList());
            compilation.setEventsId(events);
        }
        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(updatedCompilation);
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