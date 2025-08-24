package ru.yandex.practicum.ewmmainservice.main.service.user;

import ru.yandex.practicum.ewmmainservice.main.dto.user.NewUserRequest;
import ru.yandex.practicum.ewmmainservice.main.dto.user.UserDto;

import java.util.List;

public interface AdminUserService {

    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);

    UserDto getUserById(Long userId);

    boolean userExists(Long userId);

}