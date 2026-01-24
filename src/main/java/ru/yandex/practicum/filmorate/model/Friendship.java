package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;

public class Friendship {
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private Long friendId;
}
