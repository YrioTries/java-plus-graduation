package ru.practicum.explore_with_me.service.user;

import ru.practicum.explore_with_me.model.dto.user.NewUserRequest;
import ru.practicum.explore_with_me.model.dto.user.UserDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    UserDto createUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}