package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.SequenceIdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> storage = HashMap.newHashMap(100);
    private final SequenceIdGenerator idGenerator;

    @Override
    public Film create(Film film) {
        Long nextId = idGenerator.getNextId();
        film.setId(nextId);
        storage.put(nextId, film.toBuilder().build());
        log.debug("Added film: {}", film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return storage.values()
                .stream()
                .map(e -> e.toBuilder().build())
                .toList();
    }

    @Override
    public Film findById(Long filmId) {
        checkIdIsValid(filmId);
        return storage.get(filmId).toBuilder().build();
    }

    @Override
    public Film update(Film film) {
        checkIdIsValid(film.getId());
        Film originFilm = storage.put(film.getId(), film.toBuilder().build());
        log.debug("Updated film: {} to {}", originFilm, film);
        return film;
    }

    @Override
    public void delete(Long filmId) {
        checkIdIsValid(filmId);
        storage.remove(filmId);
        log.debug("Delete film with id: {}", filmId);
    }

    private void checkIdIsValid(Long filmId) {
        if (filmId == null) {
            throw new IdNotSpecifiedException();
        }
        if (!storage.containsKey(filmId)) {
            throw new NotFoundByIdException(filmId);
        }
    }
}
