package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@Repository("genreDbStorage")
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY id ASC";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_ALL_BY_FILM_ID_QUERY = "SELECT g.*" +
            " FROM films AS f" +
            " RIGHT JOIN film_genre AS fg ON fg.film_id=f.id" +
            " LEFT JOIN genres AS g ON fg.genre_id=g.id" +
            " WHERE f.id = ?";

    public GenreDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Genre> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Genre findById(Long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId)
                .orElseThrow(() -> new NotFoundByIdException(genreId));
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Collection<Genre> findByFilmId(long filmId) {
        return findMany(FIND_ALL_BY_FILM_ID_QUERY, filmId);
    }

}
