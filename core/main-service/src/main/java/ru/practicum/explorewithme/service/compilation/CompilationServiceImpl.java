package ru.practicum.explorewithme.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.model.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.model.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.model.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.model.mapper.CompilationMapper;
import ru.practicum.explorewithme.model.dao.CompilationDao;
import ru.practicum.explorewithme.model.dao.EventDao;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;


    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        CompilationDao compilation = compilationMapper.toCompilation(newCompilationDto);

        Set<EventDao> events;
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
        } else {
            events = new HashSet<>();
        }
        compilation.setEvents(events);

        CompilationDao savedCompilation = compilationRepository.save(compilation);
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
        CompilationDao compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));

        if (updateRequest.getEvents() != null) {
            Set<EventDao> events = new HashSet<>(eventRepository.findAllById(updateRequest.getEvents()));
            compilation.setEvents(events);
        }
        if (updateRequest.getPinned() != null) {
            compilation.setPinned(updateRequest.getPinned());
        }
        if (updateRequest.getTitle() != null) {
            compilation.setTitle(updateRequest.getTitle());
        }

        CompilationDao updatedCompilation = compilationRepository.save(compilation);
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
        CompilationDao compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found"));
        return compilationMapper.toCompilationDto(compilation);
    }
}