package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.SequenceIdGenerator;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    LocalDate correctDate = LocalDate.parse("1990-01-01", ISO_LOCAL_DATE);
    UserService userService = new UserService(new InMemoryUserStorage(new SequenceIdGenerator()));

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
        assertEquals(0, user.getFriendIds().size(), "Друзья");
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
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, Collections.emptySet());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, Collections.emptySet());
        User thirdUser = new User(1L, "test3@example.com", "user345", "name345", correctDate, Collections.emptySet());

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
        User originUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, Collections.emptySet());
        User savedUser = userService.create(originUser);

        User updateUser = new User(savedUser.getId(), "test2@example.com", "user234", "name234", correctDate, Collections.emptySet());
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
        User originUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, Collections.emptySet());
        userService.create(originUser);

        User updateUser = new User(null, "test2@example.com", "user234", "name234", correctDate, Collections.emptySet());
        assertThrows(IdNotSpecifiedException.class, () -> userService.update(updateUser));
    }

    @Test
    public void updateUserThrowExceptionWhenIdNotFound() {
        User originUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, Collections.emptySet());
        userService.create(originUser);

        User updateUser = new User(999L, "test2@example.com", "user234", "name234", correctDate, Collections.emptySet());
        assertThrows(NotFoundByIdException.class, () -> userService.update(updateUser));
    }

    @Test
    public void getUserByIdSuccess() {
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, Collections.emptySet());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, Collections.emptySet());
        User thirdUser = new User(1L, "test3@example.com", "user345", "name345", correctDate, Collections.emptySet());

        userService.create(firstUser);
        userService.create(secondUser);
        userService.create(thirdUser);

        User userById = userService.findById(2L);

        assertEquals(secondUser.getId(), userById.getId(), "ID второго пользователя");
        assertEquals(secondUser.getEmail(), userById.getEmail(), "Email второго пользователя");
        assertEquals(secondUser.getName(), userById.getName(), "Имя второго пользователя");
        assertEquals(secondUser.getLogin(), userById.getLogin(), "Логин второго пользователя");
        assertEquals(secondUser.getBirthday(), userById.getBirthday(), "Дата рождения второго пользователя");
    }

    @Test
    public void addFriendSuccess() {
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, new HashSet<>());
        User thirdUser = new User(1L, "test3@example.com", "user345", "name345", correctDate, new HashSet<>());

        userService.create(firstUser);
        userService.create(secondUser);
        userService.create(thirdUser);

        userService.addFriend(secondUser.getId(), thirdUser.getId());
        userService.addFriend(thirdUser.getId(), secondUser.getId());
        secondUser = userService.findById(secondUser.getId());
        thirdUser = userService.findById(thirdUser.getId());

        assertIterableEquals(List.of(thirdUser.getId()), secondUser.getFriendIds(), "Друзья второго пользователя");
        assertIterableEquals(List.of(secondUser.getId()), thirdUser.getFriendIds(), "Друзья третьего пользователя");
        assertEquals(0, firstUser.getFriendIds().size(), "Друзья первого пользователя");
    }

    @Test
    public void shouldNothingToChangeWhenRepeatedAddFriend() {
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, new HashSet<>());
        User thirdUser = new User(1L, "test3@example.com", "user345", "name345", correctDate, new HashSet<>());

        userService.create(firstUser);
        userService.create(secondUser);
        userService.create(thirdUser);

        userService.addFriend(secondUser.getId(), thirdUser.getId());
        userService.addFriend(thirdUser.getId(), secondUser.getId());
        secondUser = userService.findById(secondUser.getId());
        thirdUser = userService.findById(thirdUser.getId());

        assertIterableEquals(List.of(thirdUser.getId()), secondUser.getFriendIds(), "Друзья второго пользователя");
        assertIterableEquals(List.of(secondUser.getId()), thirdUser.getFriendIds(), "Друзья третьего пользователя");
        assertEquals(0, firstUser.getFriendIds().size(), "Друзья первого пользователя");
    }

    @Test
    public void deleteFriendSuccess() {
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, new HashSet<>());
        User thirdUser = new User(1L, "test3@example.com", "user345", "name345", correctDate, new HashSet<>());

        userService.create(firstUser);
        userService.create(secondUser);
        userService.create(thirdUser);

        userService.addFriend(secondUser.getId(), thirdUser.getId());
        userService.deleteFriend(thirdUser.getId(), secondUser.getId());

        assertEquals(0, secondUser.getFriendIds().size(), "Друзья второго пользователя");
        assertEquals(0, thirdUser.getFriendIds().size(), "Друзья третьего пользователя");
        assertEquals(0, firstUser.getFriendIds().size(), "Друзья первого пользователя");
    }

    @Test
    public void getCommonFriendSuccess() {
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, new HashSet<>());
        User thirdUser = new User(1L, "test3@example.com", "user345", "name345", correctDate, new HashSet<>());

        userService.create(firstUser);
        userService.create(secondUser);
        userService.create(thirdUser);

        userService.addFriend(secondUser.getId(), thirdUser.getId());
        userService.addFriend(thirdUser.getId(), firstUser.getId());
        userService.addFriend(firstUser.getId(), thirdUser.getId());
        userService.addFriend(thirdUser.getId(), secondUser.getId());

        Collection<User> commonFriends = userService.getCommonFriends(secondUser.getId(), firstUser.getId());
        assertIterableEquals(List.of(thirdUser), commonFriends, "Список общих друзей");
    }

    @Test
    public void getFriendsSuccess() {
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, new HashSet<>());
        User thirdUser = new User(1L, "test3@example.com", "user345", "name345", correctDate, new HashSet<>());

        userService.create(firstUser);
        userService.create(secondUser);
        userService.create(thirdUser);

        userService.addFriend(secondUser.getId(), thirdUser.getId());
        userService.addFriend(secondUser.getId(), firstUser.getId());

        Collection<User> actualFriends = userService.getFriends(secondUser.getId());
        assertIterableEquals(List.of(firstUser, thirdUser), actualFriends, "Список друзей");
    }

    @Test
    public void deleteUserSuccess() {
        User firstUser = new User(1L, "test1@example.com", "user123", "name123", correctDate, new HashSet<>());
        User secondUser = new User(1L, "test2@example.com", "user234", "name234", correctDate, new HashSet<>());
        User thirdUser = new User(1L, "test3@example.com", "user345", "name345", correctDate, new HashSet<>());

        userService.create(firstUser);
        userService.create(secondUser);
        userService.create(thirdUser);

        userService.delete(secondUser.getId());
        Collection<User> actualUsers = userService.findAll();

        assertIterableEquals(List.of(firstUser, thirdUser), actualUsers, "Список пользователей");
    }
}