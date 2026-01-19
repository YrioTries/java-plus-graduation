package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.model.dto.user.NewUserRequest;
import ru.practicum.explorewithme.model.dto.user.UserDto;
import ru.practicum.explorewithme.model.dto.user.UserShortDto;
import ru.practicum.explorewithme.model.dao.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);

    User toUser(NewUserRequest newUserRequest);
}