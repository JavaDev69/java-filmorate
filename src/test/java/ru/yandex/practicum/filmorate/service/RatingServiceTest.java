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
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.impl.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.RatingMapper;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import({RatingDbStorage.class, RatingMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class RatingServiceTest {
    private final RatingService ratingService;

    @Test
    public void testFindAll() {
        List<Rating> expectedRatings = List.of(
                new Rating(1L, "G", "Возрастных ограничений нет"),
                new Rating(2L, "PG", "Детям рекомендуется смотреть фильм с родителями"),
                new Rating(3L, "PG-13", "Детям до 13 лет просмотр не желателен"),
                new Rating(4L, "R", "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
                new Rating(5L, "NC-17", "Лицам до 18 лет просмотр запрещён"));

        Collection<Rating> ratings = ratingService.findAll();
        assertThat(ratings)
                .as("Список доступных рейтингов")
                .isNotEmpty()
                .containsExactlyElementsOf(expectedRatings);
    }

    @Test
    public void testFindByIdExisting() {
        Rating rating = ratingService.findById(1L);
        assertThat(rating).isNotNull();
        assertThat(rating.getId()).as("ID").isEqualTo(1L);
        assertThat(rating.getName()).as("Название").isEqualTo("G");
        assertThat(rating.getDescription()).as("Описание").contains("Возрастных ограничений нет");
    }

    @Test
    public void testFindByIdNonExisting() {
        assertThrows(NotFoundByIdException.class, () -> ratingService.findById(999L), "Рейтинг не найден");
    }
}