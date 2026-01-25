package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreMapper;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import({GenreDbStorage.class, GenreMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class GenreServiceTest {
    private final GenreService genreService;

    @Test
    public void testFindAll() {
        List<Genre> expectedGenres = List.of(
                new Genre(1L, "Комедия"),
                new Genre(2L, "Драма"),
                new Genre(3L, "Мультфильм"),
                new Genre(4L, "Триллер"),
                new Genre(5L, "Документальный"),
                new Genre(6L, "Боевик"));

        Collection<Genre> genres = genreService.findAll();
        assertThat(genres)
                .as("Список доступных жанров")
                .isNotEmpty()
                .containsExactlyElementsOf(expectedGenres);
    }

    @Test
    public void testFindByIdExisting() {
        Genre genre = genreService.findById(1L);
        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1L);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    public void testFindByIdNonExisting() {
        assertThrows(NotFoundByIdException.class, () -> genreService.findById(999L), "Жанр не найден");
    }
}