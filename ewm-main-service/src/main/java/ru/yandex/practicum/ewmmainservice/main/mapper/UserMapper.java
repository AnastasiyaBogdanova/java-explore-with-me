package ru.yandex.practicum.ewmmainservice.main.mapper;


import ru.yandex.practicum.ewmmainservice.main.dto.user.NewUserRequest;
import ru.yandex.practicum.ewmmainservice.main.dto.user.UserDto;
import ru.yandex.practicum.ewmmainservice.main.dto.user.UserShortDto;
import ru.yandex.practicum.ewmmainservice.main.model.User;

public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    public static UserShortDto toShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }


    public static User toUser(NewUserRequest newUserRequest) {
        return new User(
                null,
                newUserRequest.getEmail(),
                newUserRequest.getName()
        );
    }
}
