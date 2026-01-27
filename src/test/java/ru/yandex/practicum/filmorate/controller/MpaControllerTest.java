package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
class MpaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RatingService ratingService;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testFindAll() throws Exception {
        List<Rating> expectedRatings = List.of(
                new Rating(1L, "G", "Возрастных ограничений нет"),
                new Rating(2L, "PG", "Детям рекомендуется смотреть фильм с родителями"),
                new Rating(3L, "PG-13", "Детям до 13 лет просмотр не желателен"),
                new Rating(4L, "R", "Лицам до 17 лет просматривать фильм можно только в присутствии взрослого"),
                new Rating(5L, "NC-17", "Лицам до 18 лет просмотр запрещён"));

        when(ratingService.findAll()).thenReturn(expectedRatings);

        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(content().string(mapper.writeValueAsString(expectedRatings)));

        verify(ratingService, times(1)).findAll();
    }

    @Test
    public void testFindByIdExisting() throws Exception {
        Rating rating = new Rating(1L, "G", "Возрастных ограничений нет");
        when(ratingService.findById(any(Long.class))).thenReturn(rating);

        mockMvc.perform(get("/mpa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(content().string(mapper.writeValueAsString(rating)));

        verify(ratingService, times(1)).findById(1L);
    }

    @Test
    public void testFindByIdNonExisting() throws Exception {
        when(ratingService.findById(any(Long.class))).thenThrow(NotFoundByIdException.class);

        mockMvc.perform(get("/mpa/1"))
                .andExpect(status().isNotFound());

        verify(ratingService, times(1)).findById(1L);
    }
}