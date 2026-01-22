package ru.practicum.explore_with_me.service.compilation;

import ru.practicum.explore_with_me.model.dto.compilation.CompilationDto;
import ru.practicum.explore_with_me.model.dto.compilation.NewCompilationDto;
import ru.practicum.explore_with_me.model.dto.compilation.UpdateCompilationRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest);

    List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable);

    CompilationDto getCompilationById(Long compId);
}