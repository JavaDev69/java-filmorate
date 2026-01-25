package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserService {
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public User create(User user) {
        return userStorage.create(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long userId) {
        return userStorage.findById(userId);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.getFriendIds().add(friend.getId());
        userStorage.update(user);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.getFriendIds().remove(friend.getId());
        userStorage.update(user);
    }

    public Collection<User> getFriends(Long userId) {
        return findById(userId).getFriendIds()
                .stream()
                .map(this::findById)
                .toList();
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        User user = findById(userId);
        User otherUser = findById(otherId);
        return user.getFriendIds()
                .stream()
                .filter(otherUser.getFriendIds()::contains)
                .map(this::findById)
                .toList();
    }

    public User update(User updUser) {
        User targetUser = findById(updUser.getId());
        if (updUser.getName() != null) {
            targetUser.setName(updUser.getName());
        }
        if (updUser.getLogin() != null) {
            targetUser.setLogin(updUser.getLogin());
        }
        if (updUser.getEmail() != null) {
            targetUser.setEmail(updUser.getEmail());
        }
        if (updUser.getBirthday() != null) {
            targetUser.setBirthday(updUser.getBirthday());
        }
        return userStorage.update(targetUser);
    }

    public void delete(long userId) {
        userStorage.delete(userId);
    }
}
