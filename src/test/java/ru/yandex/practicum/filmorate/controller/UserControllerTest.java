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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testFindAllSuccess() throws Exception {
        List<User> users = List.of(
                new User(1L, "test1@example.com", "user123", "name123", LocalDate.of(2020, Month.DECEMBER, 8)),
                new User(2L, "test2@example.com", "user234", "name234", LocalDate.of(2010, Month.OCTOBER, 7))
        );

        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(users)));

        verify(userService, times(1)).findAll();
    }

    @Test
    public void testEmptyFindAllSuccess() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(userService, times(1)).findAll();
    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        User user = new User(10L, "test1@example.com", "user123", "name123", LocalDate.of(2020, Month.DECEMBER, 8));
        User returnUuser = new User(1L, "test1@example.com", "user123", "name123", LocalDate.of(2020, Month.DECEMBER, 8));

        when(userService.create(any(User.class))).thenReturn(returnUuser);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isCreated())
                .andExpect(content().string(mapper.writeValueAsString(returnUuser)));

        verify(userService, times(1)).create(any(User.class));
    }

    @Test
    public void testUpdateUserSuccess() throws Exception {
        User user = new User(1L, "test1@example.com", "user123", "name123", LocalDate.of(2020, Month.DECEMBER, 8));
        User returnUuser = new User(1L, "test2@example.com", "user234", "name23", LocalDate.of(2010, Month.OCTOBER, 4));

        when(userService.update(any(User.class))).thenReturn(returnUuser);
        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(returnUuser)));

        verify(userService, times(1)).update(any(User.class));
    }

    @Test
    public void testCreateUserWithoutEmailFail() throws Exception {
        User user = new User(null, "", "user123", "name123", LocalDate.of(2020, Month.DECEMBER, 8));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).create(any(User.class));
    }

    @Test
    public void testCreateUserWithoutLoginFail() throws Exception {
        User user = new User(1L, "test1@example.com", "", "name123", LocalDate.of(2020, Month.DECEMBER, 8));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).create(any(User.class));
    }

    @Test
    public void testCreateUserWithWrongEmailFail() throws Exception {
        User user = new User(1L, "test1.example.com", "efew", "name123", LocalDate.of(2020, Month.DECEMBER, 8));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).create(any(User.class));
    }
}