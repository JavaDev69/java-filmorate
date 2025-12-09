package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotFoundByIdException extends RuntimeException {
    public NotFoundByIdException(Long id) {
        super("Сущность с id = " + id + " не найдена");
    }
}
