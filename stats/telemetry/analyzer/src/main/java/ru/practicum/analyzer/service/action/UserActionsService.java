package ru.practicum.analyzer.service.action;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserActionsService {
    void updateUserActionInteractions(UserActionAvro action);
}
