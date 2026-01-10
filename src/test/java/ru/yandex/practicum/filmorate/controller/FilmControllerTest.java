package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FilmService filmService;

    private ObjectMapper mapper;
    LocalDate correctDate = LocalDate.of(2020, Calendar.DECEMBER, 8);

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testEmptyFindAllSuccess() throws Exception {
        when(filmService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(filmService, times(1)).findAll();
    }

    @Test
    public void testFindAllSuccess() throws Exception {
        List<Film> films = List.of(
                new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet()),
                new Film(2L, "name234", "desc234", correctDate, 10, Collections.emptySet())
        );

        when(filmService.findAll()).thenReturn(films);

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(content().string(mapper.writeValueAsString(films)));

        verify(filmService, times(1)).findAll();
    }

    @Test
    public void testCreateFilmSuccess() throws Exception {
        Film firstFilm = new Film(21L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film returnFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());

        when(filmService.create(any(Film.class))).thenReturn(returnFilm);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(firstFilm)))
                .andExpect(status().isCreated())
                .andExpect(content().string(mapper.writeValueAsString(returnFilm)));

        verify(filmService, times(1)).create(any(Film.class));
    }

    @Test
    public void testUpdateFilmSuccess() throws Exception {
        Film firstFilm = new Film(1L, "name123", "desc123", correctDate, 20, Collections.emptySet());
        Film returnFilm = new Film(1L, "name234", "desc234", correctDate, 10, Collections.emptySet());

        when(filmService.update(any(Film.class))).thenReturn(returnFilm);
        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(firstFilm)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(returnFilm)));

        verify(filmService, times(1)).update(any(Film.class));
    }

    @Test
    public void testCreateFilmWithoutNameFail() throws Exception {
        Film film = new Film(1L, "", "desc123", correctDate, 20, Collections.emptySet());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(film)))
                .andExpect(status().isBadRequest());

        verify(filmService, times(0)).create(any(Film.class));
    }

    @Test
    public void testCreateFilmWithDescriptionLength201Fail() throws Exception {
        Film film = new Film(1L, "name123", "a".repeat(201), correctDate, 20, Collections.emptySet());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(film)))
                .andExpect(status().isBadRequest());

        verify(filmService, times(0)).create(any(Film.class));
    }

    @Test
    public void testCreateFilmWithDescriptionLength200Success() throws Exception {
        Film film = new Film(1L, "name123", "a".repeat(200), correctDate, 20, Collections.emptySet());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(film)))
                .andExpect(status().isCreated());

        verify(filmService, times(1)).create(any(Film.class));
    }
}