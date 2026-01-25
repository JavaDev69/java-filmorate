package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmMapper;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@Import({FilmDbStorage.class, FilmMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmDbStorageTest {
    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = Film.builder()
                .name("Inception")
                .description("A mind-bending thriller")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .mpaId(1L)
                .build();
    }

    @Test
    public void testCreate() {
        Film created = filmStorage.create(testFilm);
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo(testFilm.getName());
    }

    @Test
    public void testFindById() {
        Film created = filmStorage.create(testFilm);
        Film found = filmStorage.findById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo(created.getName());
    }

    @Test
    public void testFindAll() {
        filmStorage.create(testFilm);
        Collection<Film> films = filmStorage.findAll();
        assertThat(films).isNotEmpty();
        assertThat(films).extracting(Film::getName).contains("Inception");
    }

    @Test
    public void testUpdate() {
        Film created = filmStorage.create(testFilm);
        created.setName("Updated Name");
        created.setDescription("Updated description");
        Film updated = filmStorage.update(created);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("Updated Name");

        // Проверка, что изменения сохранились в базе
        Film fetched = filmStorage.findById(updated.getId());
        assertThat(fetched.getName()).isEqualTo("Updated Name");
        assertThat(fetched.getDescription()).isEqualTo("Updated description");
    }

    @Test
    public void testDelete() {
        Film created = filmStorage.create(testFilm);
        Long id = created.getId();
        filmStorage.delete(id);

        assertThrows(NotFoundByIdException.class, () -> filmStorage.findById(id), "Фильм успешно удален");

    }
}