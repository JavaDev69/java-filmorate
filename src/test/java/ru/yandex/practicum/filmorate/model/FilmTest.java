package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValidThenNoValidationErrors() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Matrix");
        film.setDescription("A sci-fi action film.");
        film.setReleaseDate(LocalDate.parse("1999-03-31", ISO_LOCAL_DATE));
        film.setDuration(136);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenNameIsBlankThenValidationError() {
        Film film = new Film();
        film.setName("  ");
        film.setDescription("Description");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Количество ошибок валидации");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("must not be blank", violation.getMessage(), "Сообщение ошибки");
        assertEquals("name", violation.getPropertyPath().toString(), "Поле");
    }

    @Test
    public void whenDescriptionLength201ThenValidationError() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("x".repeat(201));
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Количество ошибок валидации");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("size must be between 0 and 200", violation.getMessage(), "Сообщение ошибки");
        assertEquals("description", violation.getPropertyPath().toString(), "Поле");
    }

    @Test
    public void whenDurationNotPositiveThenValidationError() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("Valid description");
        film.setDuration(0);
        film.setReleaseDate(LocalDate.now());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Количество ошибок валидации");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("must be greater than 0", violation.getMessage(), "Сообщение ошибки");
        assertEquals("duration", violation.getPropertyPath().toString(), "Поле");
    }

    @Test
    public void whenReleaseDateInvalidThenValidationError() {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.parse("1895-12-27", ISO_LOCAL_DATE));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Количество ошибок валидации");
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Дата должна быть после 28.12.1895", violation.getMessage(), "Сообщение ошибки");
        assertEquals("releaseDate", violation.getPropertyPath().toString(), "Поле");
    }

    @Test
    public void whenReleaseDateValidThenNoValidationError() {

        Film film = new Film();
        film.setName("Movie");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.parse("1895-12-28", ISO_LOCAL_DATE));
        film.setDuration(100);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Ошибок валидации нет");
    }
}