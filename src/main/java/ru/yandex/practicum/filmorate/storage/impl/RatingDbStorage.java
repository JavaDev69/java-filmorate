package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;

@Repository("ratingDbStorage")
public class RatingDbStorage extends BaseRepository<Rating> implements RatingStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM ratings ORDER BY id ASC";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM ratings WHERE id = ?";

    public RatingDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Rating> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public Rating findById(Long ratingId) {
        return findOne(FIND_BY_ID_QUERY, ratingId)
                .orElseThrow(() -> new NotFoundByIdException(ratingId));
    }

    @Override
    public Collection<Rating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

}
