package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.SequenceIdGenerator;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    LocalDate correctDate = LocalDate.of(2020, Calendar.DECEMBER, 8);
    UserService userService = new UserService(new InMemoryUserStorage(new SequenceIdGenerator()));
    FilmService filmService = new FilmService(new InMemoryFilmStorage(new SequenceIdGenerator()), userService);

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
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film secondFilm = new Film(1L, "name234", "desc234", correctDate, 10, Collections.emptySet());

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
        Film originFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film savedFilm = filmService.create(originFilm);

        Film updateFilm = new Film(savedFilm.getId(), "name234", "desc234", correctDate, 10, Collections.emptySet());
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
        Film originFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        filmService.create(originFilm);

        Film updateFilm = new Film(null, "name234", "desc234", correctDate, 10, Collections.emptySet());
        assertThrows(IdNotSpecifiedException.class, () -> filmService.update(updateFilm));
    }

    @Test
    public void updateFilmThrowExceptionWhenIdNotFound() {
        Film originFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        filmService.create(originFilm);

        Film updateFilm = new Film(45L, "name234", "desc234", correctDate, 10, Collections.emptySet());
        assertThrows(NotFoundByIdException.class, () -> filmService.update(updateFilm));
    }

    @Test
    public void deleteFilmSuccess() {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film secondFilm = new Film(1L, "name234", "desc234", correctDate, 10, Collections.emptySet());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        filmService.delete(firstFilm.getId());

        Collection<Film> actual = filmService.findAll();

        assertIterableEquals(List.of(secondFilm), actual, "Количество фильмов");
    }

    @Test
    public void getFilmByIdSuccess() {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film secondFilm = new Film(1L, "name234", "desc234", correctDate, 10, Collections.emptySet());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        Film filmById = filmService.findById(2L);

        assertEquals(secondFilm.getId(), filmById.getId(), "ID");
        assertEquals(secondFilm.getName(), filmById.getName(), "Название");
        assertEquals(secondFilm.getDescription(), filmById.getDescription(), "Описание");
        assertEquals(secondFilm.getReleaseDate(), filmById.getReleaseDate(), "Дата релиза");
        assertEquals(secondFilm.getDuration(), filmById.getDuration(), "Продолжительность");
        assertEquals(0, filmById.getUserLikeIds().size(), "Количество лайков");
    }

    @Test
    public void addLikeSuccess() {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film secondFilm = new Film(1L, "name234", "desc234", correctDate, 10, Collections.emptySet());
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        userService.create(firstUser);

        filmService.addLike(firstFilm.getId(), firstUser.getId());

        Film actual = filmService.findById(firstFilm.getId());

        assertIterableEquals(List.of(firstUser.getId()), actual.getUserLikeIds(), "Количество лайков");
    }

    @Test
    public void repeatAddLikeSuccess() {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film secondFilm = new Film(1L, "name234", "desc234", correctDate, 10, Collections.emptySet());
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        userService.create(firstUser);

        filmService.addLike(firstFilm.getId(), firstUser.getId());
        filmService.addLike(firstFilm.getId(), firstUser.getId());

        Film actual = filmService.findById(firstFilm.getId());

        assertIterableEquals(List.of(firstUser.getId()), actual.getUserLikeIds(), "Количество лайков");
    }

    @Test
    public void deleteLikeSuccess() {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());

        filmService.create(firstFilm);
        userService.create(firstUser);

        filmService.addLike(firstFilm.getId(), firstUser.getId());
        filmService.deleteLike(firstFilm.getId(), firstUser.getId());

        Film actual = filmService.findById(firstFilm.getId());

        assertEquals(0, actual.getUserLikeIds().size(), "Количество лайков");
    }

    @Test
    public void findPopularSuccess() {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film secondFilm = new Film(1L, "name234", "desc234", correctDate, 10, Collections.emptySet());
        Film thirdFilm = new Film(1L, "name345", "desc345", correctDate, 101, Collections.emptySet());
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, Collections.emptySet());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        filmService.create(thirdFilm);

        userService.create(firstUser);
        userService.create(secondUser);

        filmService.addLike(thirdFilm.getId(), firstUser.getId());
        filmService.addLike(secondFilm.getId(), firstUser.getId());
        filmService.addLike(secondFilm.getId(), secondFilm.getId());

        Collection<Film> actual = filmService.findPopular(5);

        assertIterableEquals(List.of(secondFilm, thirdFilm, firstFilm), actual, "Порядок фильмов и количество");
    }

    @Test
    public void findPopularWhenCountTwoSuccess() {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film secondFilm = new Film(1L, "name234", "desc234", correctDate, 10, Collections.emptySet());
        Film thirdFilm = new Film(1L, "name345", "desc345", correctDate, 101, Collections.emptySet());
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, Collections.emptySet());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        filmService.create(thirdFilm);

        userService.create(firstUser);
        userService.create(secondUser);

        filmService.addLike(thirdFilm.getId(), firstUser.getId());
        filmService.addLike(secondFilm.getId(), firstUser.getId());
        filmService.addLike(secondFilm.getId(), secondFilm.getId());

        Collection<Film> actual = filmService.findPopular(2);

        assertIterableEquals(List.of(secondFilm, thirdFilm), actual, "Порядок фильмов и количество");
    }

    @Test
    public void findPopularWhenNotLikesSuccess() {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film secondFilm = new Film(1L, "name234", "desc234", correctDate, 10, Collections.emptySet());
        Film thirdFilm = new Film(1L, "name345", "desc345", correctDate, 101, Collections.emptySet());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        filmService.create(thirdFilm);

        Collection<Film> actual = filmService.findPopular(3);

        assertIterableEquals(List.of(firstFilm, secondFilm, thirdFilm), actual, "Порядок фильмов и количество");
    }
}