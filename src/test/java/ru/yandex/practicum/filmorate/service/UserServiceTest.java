package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    LocalDate correctDate = LocalDate.parse("1990-01-01", ISO_LOCAL_DATE);
    UserService userService = new UserService();

    @Test
    public void createUserSuccess() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user123");
        user.setName("John Doe");
        user.setBirthday(correctDate);

        User actual = userService.create(user);

        assertEquals(1L, actual.getId(), "ID");
        assertEquals(user.getEmail(), actual.getEmail(), "Email");
        assertEquals(user.getName(), actual.getName(), "Имя");
        assertEquals(user.getLogin(), actual.getLogin(), "Логин");
        assertEquals(user.getBirthday(), actual.getBirthday(), "Дата рождения");
    }

    @Test
    public void createUserSuccessWhenIdSpecified() {
        User user = new User();
        user.setId(20L);
        user.setEmail("test@example.com");
        user.setLogin("user123");
        user.setName("John Doe");
        user.setBirthday(correctDate);

        User actual = userService.create(user);

        assertEquals(1L, actual.getId(), "ID");
        assertEquals(user.getEmail(), actual.getEmail(), "Email");
        assertEquals(user.getName(), actual.getName(), "Имя");
        assertEquals(user.getLogin(), actual.getLogin(), "Логин");
        assertEquals(user.getBirthday(), actual.getBirthday(), "Дата рождения");
    }

    @Test
    public void createUserSuccessWhenNameIsBlank() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user123");
        user.setBirthday(correctDate);

        User actual = userService.create(user);

        assertEquals(1L, actual.getId(), "ID");
        assertEquals(user.getEmail(), actual.getEmail(), "Email");
        assertEquals(user.getLogin(), actual.getName(), "Имя");
        assertEquals(user.getLogin(), actual.getLogin(), "Логин");
        assertEquals(user.getBirthday(), actual.getBirthday(), "Дата рождения");
    }

    @Test
    public void getAllUsersSuccessWhenEmpty() {
        Collection<User> actual = userService.findAll();

        assertTrue(actual.isEmpty(), "Список пользователей");
    }

    @Test
    public void getAllUserSuccess() {
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate);
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate);
        User thirdUser = new User(1L, "test3@example.com", "user345", "name345", correctDate);

        userService.create(firstUser);
        userService.create(secondUser);
        userService.create(thirdUser);

        Collection<User> actualUsers = userService.findAll();
        var users = new ArrayList<>(actualUsers);
        assertEquals(firstUser.getId(), users.getFirst().getId(), "ID первого пользователя");
        assertEquals(firstUser.getEmail(), users.getFirst().getEmail(), "Email первого пользователя");
        assertEquals(firstUser.getName(), users.getFirst().getName(), "Имя первого пользователя");
        assertEquals(firstUser.getLogin(), users.getFirst().getLogin(), "Логин первого пользователя");
        assertEquals(firstUser.getBirthday(), users.getFirst().getBirthday(), "Дата рождения первого пользователя");

        assertEquals(secondUser.getId(), users.get(1).getId(), "ID второго пользователя");
        assertEquals(secondUser.getEmail(), users.get(1).getEmail(), "Email второго пользователя");
        assertEquals(secondUser.getName(), users.get(1).getName(), "Имя второго пользователя");
        assertEquals(secondUser.getLogin(), users.get(1).getLogin(), "Логин второго пользователя");
        assertEquals(secondUser.getBirthday(), users.get(1).getBirthday(), "Дата рождения второго пользователя");

        assertEquals(thirdUser.getId(), users.getLast().getId(), "ID третьего пользователя");
        assertEquals(thirdUser.getEmail(), users.getLast().getEmail(), "Email третьего пользователя");
        assertEquals(thirdUser.getName(), users.getLast().getName(), "Имя третьего пользователя");
        assertEquals(thirdUser.getLogin(), users.getLast().getLogin(), "Логин третьего пользователя");
        assertEquals(thirdUser.getBirthday(), users.getLast().getBirthday(), "Дата рождения третьего пользователя");
    }

    @Test
    public void updateUserSuccess() {
        User originUser = new User(1L, "test1@example.com", "user123", "name123", correctDate);
        User savedUser = userService.create(originUser);

        User updateUser = new User(savedUser.getId(), "test2@example.com", "user234", "name234", correctDate);
        userService.update(updateUser);

        Collection<User> actual = userService.findAll();

        assertEquals(1, actual.size(), "Количество пользователей");
        ArrayList<User> users = new ArrayList<>(actual);
        assertEquals(updateUser.getId(), users.getFirst().getId(), "ID пользователя");
        assertEquals(updateUser.getEmail(), users.getFirst().getEmail(), "Email пользователя");
        assertEquals(updateUser.getName(), users.getFirst().getName(), "Имя пользователя");
        assertEquals(updateUser.getLogin(), users.getFirst().getLogin(), "Логин пользователя");
        assertEquals(updateUser.getBirthday(), users.getFirst().getBirthday(), "Дата рождения пользователя");
    }

    @Test
    public void updateUserThrowExceptionWhenIdNull() {
        User originUser = new User(1L, "test1@example.com", "user123", "name123", correctDate);
        userService.create(originUser);

        User updateUser = new User(null, "test2@example.com", "user234", "name234", correctDate);
        assertThrows(IdNotSpecifiedException.class, () -> userService.update(updateUser));
    }

    @Test
    public void updateUserThrowExceptionWhenIdNotFound() {
        User originUser = new User(1L, "test1@example.com", "user123", "name123", correctDate);
        userService.create(originUser);

        User updateUser = new User(999L, "test2@example.com", "user234", "name234", correctDate);
        assertThrows(NotFoundByIdException.class, () -> userService.update(updateUser));
    }
}