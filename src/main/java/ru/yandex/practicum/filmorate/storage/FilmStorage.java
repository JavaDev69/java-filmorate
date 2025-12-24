package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film findById(Long filmId);

    Collection<Film> findAll();

    Film update(Film film);

    void delete(Long id);
}
