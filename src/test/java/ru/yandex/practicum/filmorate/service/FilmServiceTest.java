package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    LocalDate correctDate = LocalDate.of(2020, Calendar.DECEMBER, 8);
    FilmService filmService = new FilmService();

    @Test
    public void createFilmSuccess() {
        Film film = new Film();
        film.setName("test@example.com");
        film.setDescription("user123");
        film.setReleaseDate(correctDate);
        film.setDuration(30);

        Film actual = filmService.create(film);

        assertEquals(1L, actual.getId(), "ID");
        assertEquals(film.getName(), actual.getName(), "Название");
        assertEquals(film.getDescription(), actual.getDescription(), "Описание");
        assertEquals(film.getReleaseDate(), actual.getReleaseDate(), "Дата релиза");
        assertEquals(film.getDuration(), actual.getDuration(), "Продолжительность");
    }

    @Test
    public void createFilmSuccessWhenIdSpecified() {
        Film film = new Film();
        film.setId(9L);
        film.setName("test@example.com");
        film.setDescription("user123");
        film.setReleaseDate(correctDate);
        film.setDuration(30);

        Film actual = filmService.create(film);

        assertEquals(1L, actual.getId(), "ID");
        assertEquals(film.getName(), actual.getName(), "Название");
        assertEquals(film.getDescription(), actual.getDescription(), "Описание");
        assertEquals(film.getReleaseDate(), actual.getReleaseDate(), "Дата релиза");
        assertEquals(film.getDuration(), actual.getDuration(), "Продолжительность");
    }

    @Test
    public void getAllFilmsSuccessWhenEmpty() {
        Collection<Film> actual = filmService.findAll();

        assertTrue(actual.isEmpty(), "Список фильмов");
    }

    @Test
    public void getAllFilmsSuccess() {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20);
        Film secondFilm = new Film(1L, "name234", "desc234", correctDate, 10);

        filmService.create(firstFilm);
        filmService.create(secondFilm);

        Collection<Film> actual = filmService.findAll();
        var films = new ArrayList<>(actual);

        assertEquals(firstFilm.getId(), films.getFirst().getId(), "ID");
        assertEquals(firstFilm.getName(), films.getFirst().getName(), "Название");
        assertEquals(firstFilm.getDescription(), films.getFirst().getDescription(), "Описание");
        assertEquals(firstFilm.getReleaseDate(), films.getFirst().getReleaseDate(), "Дата релиза");
        assertEquals(firstFilm.getDuration(), films.getFirst().getDuration(), "Продолжительность");

        assertEquals(secondFilm.getId(), films.get(1).getId(), "ID");
        assertEquals(secondFilm.getName(), films.get(1).getName(), "Название");
        assertEquals(secondFilm.getDescription(), films.get(1).getDescription(), "Описание");
        assertEquals(secondFilm.getReleaseDate(), films.get(1).getReleaseDate(), "Дата релиза");
        assertEquals(secondFilm.getDuration(), films.get(1).getDuration(), "Продолжительность");
    }

    @Test
    public void updateFilmSuccess() {
        Film originFilm = new Film(1L, "name123", "desc123", correctDate, 20);
        Film savedFilm = filmService.create(originFilm);

        Film updateFilm = new Film(savedFilm.getId(), "name234", "desc234", correctDate, 10);
        filmService.update(updateFilm);

        Collection<Film> actual = filmService.findAll();

        assertEquals(1, actual.size(), "Количество фильмов");
        var films = new ArrayList<>(actual);
        assertEquals(originFilm.getId(), films.getFirst().getId(), "ID");
        assertEquals(updateFilm.getName(), films.getFirst().getName(), "Название");
        assertEquals(updateFilm.getDescription(), films.getFirst().getDescription(), "Описание");
        assertEquals(updateFilm.getReleaseDate(), films.getFirst().getReleaseDate(), "Дата релиза");
        assertEquals(updateFilm.getDuration(), films.getFirst().getDuration(), "Продолжительность");
    }

    @Test
    public void updateFilmThrowExceptionWhenIdNull() {
        Film originFilm = new Film(1L, "name123", "desc123", correctDate, 20);
        filmService.create(originFilm);

        Film updateFilm = new Film(null, "name234", "desc234", correctDate, 10);
        assertThrows(IdNotSpecifiedException.class, () -> filmService.update(updateFilm));
    }

    @Test
    public void updateFilmThrowExceptionWhenIdNotFound() {
        Film originFilm = new Film(1L, "name123", "desc123", correctDate, 20);
        filmService.create(originFilm);

        Film updateFilm = new Film(45L, "name234", "desc234", correctDate, 10);
        assertThrows(NotFoundByIdException.class, () -> filmService.update(updateFilm));
    }
}