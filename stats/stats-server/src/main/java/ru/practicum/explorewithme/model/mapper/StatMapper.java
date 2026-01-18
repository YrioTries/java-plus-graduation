package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatResponseDto;
import ru.practicum.explorewithme.model.dao.StatDao;

@Mapper(componentModel = "spring")
public interface StatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "timestamp", target = "created")
    StatDao toStat(EndpointHitDto endpointHitDto);

    StatResponseDto toStatResponseDto(StatDao stat);
}