package ru.practicum.explore_with_me.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore_with_me.model.dto.user.NewUserRequest;
import ru.practicum.explore_with_me.model.dto.user.UserDto;
import ru.practicum.explore_with_me.model.dto.user.UserShortDto;
import ru.practicum.explore_with_me.model.dao.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);

    User toUser(NewUserRequest newUserRequest);
}