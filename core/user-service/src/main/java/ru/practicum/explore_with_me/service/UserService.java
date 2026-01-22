package ru.practicum.explore_with_me.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore_with_me.interaction_api.model.user.dto.NewUserRequest;
import ru.practicum.explore_with_me.interaction_api.model.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
