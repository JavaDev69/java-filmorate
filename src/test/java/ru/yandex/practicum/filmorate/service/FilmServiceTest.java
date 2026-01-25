package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.storage.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserMapper;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({FilmDbStorage.class, UserDbStorage.class, RatingDbStorage.class, RatingMapper.class, GenreDbStorage.class,
        GenreMapper.class, UserMapper.class, FilmMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class FilmServiceTest {
    LocalDate correctDate = LocalDate.of(2020, Calendar.DECEMBER, 8);
    private final UserService userService;
    private final FilmService filmService;

    @Test
    public void createFilmSuccess() {
        FilmDto film = new FilmDto();
        film.setName("test@example.com");
        film.setDescription("user123");
        film.setReleaseDate(correctDate);
        film.setDuration(30);

        FilmDto actual = filmService.create(film);

        assertEquals(1L, actual.getId(), "ID");
        assertEquals(film.getName(), actual.getName(), "Название");
        assertEquals(film.getDescription(), actual.getDescription(), "Описание");
        assertEquals(film.getReleaseDate(), actual.getReleaseDate(), "Дата релиза");
        assertEquals(film.getDuration(), actual.getDuration(), "Продолжительность");
    }

    @Test
    public void createFilmSuccessWhenIdSpecified() {
        FilmDto film = new FilmDto();
        film.setId(9L);
        film.setName("test@example.com");
        film.setDescription("user123");
        film.setReleaseDate(correctDate);
        film.setDuration(30);

        FilmDto actual = filmService.create(film);

        assertEquals(1L, actual.getId(), "ID");
        assertEquals(film.getName(), actual.getName(), "Название");
        assertEquals(film.getDescription(), actual.getDescription(), "Описание");
        assertEquals(film.getReleaseDate(), actual.getReleaseDate(), "Дата релиза");
        assertEquals(film.getDuration(), actual.getDuration(), "Продолжительность");
    }

    @Test
    public void getAllFilmsSuccessWhenEmpty() {
        Collection<FilmDto> actual = filmService.findAll();

        assertTrue(actual.isEmpty(), "Список фильмов");
    }

    @Test
    public void getAllFilmsSuccess() {
        FilmDto firstFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        FilmDto secondFilm = new FilmDto(1L, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), Collections.emptyList());

        filmService.create(firstFilm);
        filmService.create(secondFilm);

        Collection<FilmDto> actual = filmService.findAll();
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
        FilmDto originFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        FilmDto savedFilm = filmService.create(originFilm);

        FilmDto updateFilm = new FilmDto(savedFilm.getId(), "name234", "desc234", correctDate, 10, null, Collections.emptyList(), Collections.emptyList());
        filmService.update(updateFilm);

        Collection<FilmDto> actual = filmService.findAll();

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
        FilmDto originFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        filmService.create(originFilm);

        FilmDto updateFilm = new FilmDto(null, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), Collections.emptyList());
        assertThrows(IdNotSpecifiedException.class, () -> filmService.update(updateFilm));
    }

    @Test
    public void updateFilmThrowExceptionWhenIdNotFound() {
        FilmDto originFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        filmService.create(originFilm);

        FilmDto updateFilm = new FilmDto(45L, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), Collections.emptyList());
        assertThrows(NotFoundByIdException.class, () -> filmService.update(updateFilm));
    }

    @Test
    public void deleteFilmSuccess() {
        FilmDto firstFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        FilmDto secondFilm = new FilmDto(1L, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), Collections.emptyList());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        filmService.delete(firstFilm.getId());

        Collection<FilmDto> actual = filmService.findAll();

        assertIterableEquals(List.of(secondFilm), actual, "Количество фильмов");
    }

    @Test
    public void getFilmByIdSuccess() {
        FilmDto firstFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        FilmDto secondFilm = new FilmDto(1L, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), Collections.emptyList());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        FilmDto filmById = filmService.findById(2L);

        assertEquals(secondFilm.getId(), filmById.getId(), "ID");
        assertEquals(secondFilm.getName(), filmById.getName(), "Название");
        assertEquals(secondFilm.getDescription(), filmById.getDescription(), "Описание");
        assertEquals(secondFilm.getReleaseDate(), filmById.getReleaseDate(), "Дата релиза");
        assertEquals(secondFilm.getDuration(), filmById.getDuration(), "Продолжительность");
        assertEquals(0, filmById.getLikes().size(), "Количество лайков");
    }

    @Test
    public void addLikeSuccess() {
        FilmDto firstFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        FilmDto secondFilm = new FilmDto(1L, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), Collections.emptyList());
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        userService.create(firstUser);

        filmService.addLike(firstFilm.getId(), firstUser.getId());

        FilmDto actual = filmService.findById(firstFilm.getId());

        assertIterableEquals(List.of(firstUser.getId()), actual.getLikes(), "Количество лайков");
    }

    @Test
    public void repeatAddLikeSuccess() {
        FilmDto firstFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        FilmDto secondFilm = new FilmDto(1L, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), Collections.emptyList());
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        userService.create(firstUser);

        filmService.addLike(firstFilm.getId(), firstUser.getId());
        filmService.addLike(firstFilm.getId(), firstUser.getId());

        FilmDto actual = filmService.findById(firstFilm.getId());

        assertIterableEquals(List.of(firstUser.getId()), actual.getLikes(), "Количество лайков");
    }

    @Test
    public void deleteLikeSuccess() {
        FilmDto firstFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());

        filmService.create(firstFilm);
        userService.create(firstUser);

        filmService.addLike(firstFilm.getId(), firstUser.getId());
        filmService.deleteLike(firstFilm.getId(), firstUser.getId());

        FilmDto actual = filmService.findById(firstFilm.getId());

        assertEquals(0, actual.getLikes().size(), "Количество лайков");
    }

    @Test
    public void findPopularSuccess() {
        FilmDto firstFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        FilmDto secondFilm = new FilmDto(1L, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), List.of(1L, 2L));
        FilmDto thirdFilm = new FilmDto(1L, "name345", "desc345", correctDate, 101, null, Collections.emptyList(), List.of(1L));
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

        Collection<FilmDto> actual = filmService.findPopular(5);

        assertIterableEquals(List.of(secondFilm, thirdFilm, firstFilm), actual, "Порядок фильмов и количество");
    }

    @Test
    public void findPopularWhenCountTwoSuccess() {
        FilmDto firstFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        FilmDto secondFilm = new FilmDto(1L, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), List.of(1L, 2L));
        FilmDto thirdFilm = new FilmDto(1L, "name345", "desc345", correctDate, 101, null, Collections.emptyList(), List.of(1L));
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

        Collection<FilmDto> actual = filmService.findPopular(2);

        assertIterableEquals(List.of(secondFilm, thirdFilm), actual, "Порядок фильмов и количество");
    }

    @Test
    public void findPopularWhenNotLikesSuccess() {
        FilmDto firstFilm = new FilmDto(1L, "name123", "desc123", correctDate, 20, null, Collections.emptyList(), Collections.emptyList());
        FilmDto secondFilm = new FilmDto(1L, "name234", "desc234", correctDate, 10, null, Collections.emptyList(), Collections.emptyList());
        FilmDto thirdFilm = new FilmDto(1L, "name345", "desc345", correctDate, 101, null, Collections.emptyList(), Collections.emptyList());

        filmService.create(firstFilm);
        filmService.create(secondFilm);
        filmService.create(thirdFilm);

        Collection<FilmDto> actual = filmService.findPopular(3);

        assertIterableEquals(List.of(firstFilm, secondFilm, thirdFilm), actual, "Порядок фильмов и количество");
    }
}
