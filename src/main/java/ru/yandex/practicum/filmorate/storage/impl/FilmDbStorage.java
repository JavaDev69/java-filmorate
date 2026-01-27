package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository("filmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = FIND_ALL_QUERY + " WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
            "WHERE id = ?";
    private static final String ADD_GENRES_TO_FILM_QUERY = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_BY_FILM_ID_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String FIND_LIKES_BY_FILM_ID_QUERY = "SELECT user_id FROM film_like WHERE film_id = ?";
    private static final String ADD_LIKES_TO_FILM_QUERY = "INSERT INTO film_like(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKES_BY_FILM_ID_QUERY = "DELETE FROM film_like WHERE film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Film> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Film create(Film film) {
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpaId());

        film.setId(id);
        addGenreToFilm(id, film.getGenres());
        addLikesToFilm(id, film.getUserLikeIds());
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpaId(),
                film.getId());

        if (film.getGenres() != null) {
            deleteGenresByFilmId(film.getId());
            addGenreToFilm(film.getId(), film.getGenres());
        }

        if (film.getUserLikeIds() != null) {
            deleteLikesByFilmId(film.getId());
            addLikesToFilm(film.getId(), film.getUserLikeIds());
        }
        return film;
    }

    @Override
    public Film findById(Long filmId) {
        Film film = findOne(FIND_BY_ID_QUERY, filmId)
                .orElseThrow(() -> new NotFoundByIdException(filmId));

        List<Long> likesByFilmId = getLikesByFilmId(filmId);
        film.getUserLikeIds().addAll(likesByFilmId);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return findMany(FIND_ALL_QUERY).stream()
                .peek(film -> {
                    List<Long> likesByFilmId = getLikesByFilmId(film.getId());
                    film.getUserLikeIds().addAll(likesByFilmId);
                })
                .toList();
    }

    public List<Long> getLikesByFilmId(long filmId) {
        return jdbcTemplate.queryForList(FIND_LIKES_BY_FILM_ID_QUERY, Long.class, filmId);
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_BY_ID_QUERY, id);
    }

    private void deleteGenresByFilmId(long filmId) {
        delete(DELETE_GENRES_BY_FILM_ID_QUERY, filmId);
    }

    private void addGenreToFilm(long filmId, Set<Genre> genres) {
        genres
                .stream()
                .map(Genre::getId)
                .forEach(genreId -> update(ADD_GENRES_TO_FILM_QUERY, filmId, genreId));
    }

    private void deleteLikesByFilmId(long filmId) {
        delete(DELETE_LIKES_BY_FILM_ID_QUERY, filmId);
    }

    private void addLikesToFilm(long filmId, Set<Long> likes) {
        likes.forEach(userId -> update(ADD_LIKES_TO_FILM_QUERY, filmId, userId));
    }
}
