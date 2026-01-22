package ru.practicum.explore_with_me.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.model.dto.compilation.CompilationDto;
import ru.practicum.explore_with_me.model.dto.compilation.NewCompilationDto;
import ru.practicum.explore_with_me.model.dao.Compilation;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toCompilation(NewCompilationDto newCompilationDto);

    CompilationDto toCompilationDto(Compilation compilation);
}