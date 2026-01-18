package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.model.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.model.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.model.dao.CompilationDao;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    CompilationDao toCompilation(NewCompilationDto newCompilationDto);

    CompilationDto toCompilationDto(CompilationDao compilation);
}