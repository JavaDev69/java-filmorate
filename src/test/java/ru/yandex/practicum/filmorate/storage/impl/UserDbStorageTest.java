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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserMapper;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@Import({UserDbStorage.class, UserMapper.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserDbStorageTest {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setLogin("johndoe");
        testUser.setBirthday(LocalDate.of(1999, Month.JANUARY, 1));
    }

    @Test
    public void testUserEmptyUsersAfterDelete() {
        Collection<User> users = userStorage.findAll();

        assertThat(users)
                .as("Список пользователей пуст")
                .isEmpty();
    }

    @Test
    void testUserCreate() {
        User createdUser = userStorage.create(testUser);
        assertThat(createdUser.getId())
                .as("ID")
                .isNotNull()
                .isEqualTo(1L);
        assertThat(createdUser.getName()).as("Имя").isEqualTo(testUser.getName());
        assertThat(createdUser.getEmail()).as("Email").isEqualTo(testUser.getEmail());
    }

    @Test
    void testUserFindAll() {
        userStorage.create(testUser);
        Collection<User> users = userStorage.findAll();
        assertThat(users).as("Список пользователей не пустой").isNotEmpty();
        assertThat(users).as("Список пользователей").extracting(User::getName).contains("John Doe");
    }

    @Test
    void testUserFindById() {
        User createdUser = userStorage.create(testUser);
        User fetchedUser = userStorage.findById(createdUser.getId());
        assertThat(fetchedUser).isNotNull();
        assertThat(fetchedUser.getId()).as("ID").isEqualTo(createdUser.getId());
        assertThat(fetchedUser.getName()).as("Имя").isEqualTo(createdUser.getName());
    }

    @Test
    void testUserUpdate() {
        User createdUser = userStorage.create(testUser);
        createdUser.setName("Jane Doe");

        User updatedUser = userStorage.update(createdUser);
        assertThat(updatedUser.getName()).as("Имя").isEqualTo("Jane Doe");

        User fetchedUser = userStorage.findById(updatedUser.getId());
        assertThat(fetchedUser.getName()).as("Имя").isEqualTo("Jane Doe");
    }

    @Test
    void testUserDelete() {
        User createdUser = userStorage.create(testUser);
        Long userId = createdUser.getId();
        userStorage.delete(userId);

        assertThrows(NotFoundByIdException.class, () -> userStorage.findById(userId), "Пользователь успешно удален");
    }
}