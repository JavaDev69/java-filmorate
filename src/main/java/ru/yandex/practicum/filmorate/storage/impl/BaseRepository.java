package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseRepository<T> {
    protected final JdbcTemplate jdbcTemplate;
    protected final RowMapper<T> mapper;

    protected List<T> findMany(String query, Object... params) {
        return jdbcTemplate.query(query, mapper, params);
    }

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbcTemplate.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    protected boolean delete(String query, Object... params) {
        int rowDeleted = jdbcTemplate.update(query, params);
        return rowDeleted > 0;
    }

    protected void update(String query, Object... params) {
        int rowUpdated = jdbcTemplate.update(query, params);
        if (rowUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new InternalServerException("Не удалось сохранить данные");
        }

        return id;
    }
}
