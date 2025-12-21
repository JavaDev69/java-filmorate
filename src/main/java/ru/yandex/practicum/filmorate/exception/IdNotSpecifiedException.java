package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IdNotSpecifiedException extends RuntimeException {
    public IdNotSpecifiedException() {
        super("Не указан Id для сущности");
    }
}
