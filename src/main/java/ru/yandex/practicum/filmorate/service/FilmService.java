package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FilmService {
    private final Map<Long, Film> storage = HashMap.newHashMap(100);

    public Film create(Film film) {
        Long nextId = getNextId();
        film.setId(nextId);
        storage.put(nextId, film);
        log.debug("Added film: {}", film);
        return film;
    }

    public Collection<Film> findAll() {
        return storage.values();
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            throw new IdNotSpecifiedException();
        }
        if (!storage.containsKey(film.getId())) {
            throw new NotFoundByIdException(film.getId());
        }
        Film originFilm = storage.put(film.getId(), film);
        log.debug("Updated film: {} to {}", originFilm, film);
        return film;
    }

    private Long getNextId() {
        long currentMaxId = storage.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        return ++currentMaxId;
    }
}
