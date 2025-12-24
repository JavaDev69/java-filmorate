package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class UserService {
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
        friend.getFriendIds().add(user.getId());
        update(user);
        update(friend);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        user.getFriendIds().remove(friend.getId());
        friend.getFriendIds().remove(user.getId());
        update(user);
        update(friend);
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

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(long userId) {
        userStorage.delete(userId);
    }
}
