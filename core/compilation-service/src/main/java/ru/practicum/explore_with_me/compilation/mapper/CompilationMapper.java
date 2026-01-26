package ru.practicum.explore_with_me.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.compilation.dao.Compilation;
import ru.practicum.explore_with_me.interaction_api.model.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.interaction_api.model.compilation.dto.NewCompilationDto;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventsId", source = "events")
    Compilation toCompilation(NewCompilationDto dto);

    @Mapping(target = "events", ignore = true)
    CompilationDto toCompilationDto(Compilation compilation);
}

