package ru.practicum.explore_with_me.model.dto.compilation;

import ru.practicum.explore_with_me.model.dto.event.EventShortDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Set<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    private String title;
}