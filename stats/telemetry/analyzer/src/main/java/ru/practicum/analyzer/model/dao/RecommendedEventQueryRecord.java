package ru.practicum.analyzer.model.dao;

public record RecommendedEventQueryRecord(
        Long eventId,
        Double score
) {
}
