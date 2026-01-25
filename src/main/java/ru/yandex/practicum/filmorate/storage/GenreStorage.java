package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreStorage {
    Collection<Genre> findAll();

    Collection<Genre> findByFilmId(long filmId);

    Genre findById(Long genreId);
}
