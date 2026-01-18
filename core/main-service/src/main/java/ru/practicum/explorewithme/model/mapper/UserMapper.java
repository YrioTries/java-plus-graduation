package ru.practicum.explorewithme.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.model.dto.user.NewUserRequest;
import ru.practicum.explorewithme.model.dto.user.UserDto;
import ru.practicum.explorewithme.model.dto.user.UserShortDto;
import ru.practicum.explorewithme.model.dao.UserDao;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(UserDao user);

    UserShortDto toUserShortDto(UserDao user);

    UserDao toUser(NewUserRequest newUserRequest);
}