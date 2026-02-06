package ru.practicum.analyzer.dao;

public record RecommendedEventQueryRecord(
        Long eventId,
        Double score
) {
}
