package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.SequenceIdGenerator;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> storage = HashMap.newHashMap(100);
    private final SequenceIdGenerator idGenerator;

    @Override
    public User create(User user) {
        Long nextId = idGenerator.getNextId();
        user.setId(nextId);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        storage.put(nextId, user.toBuilder().build());
        log.debug("Added user: {}", user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return storage.values()
                .stream()
                .map(e -> e.toBuilder().build())
                .toList();
    }

    @Override
    public User findById(Long userId) {
        checkIdIsValid(userId);
        User userById = storage.get(userId);
        return userById.toBuilder().build();
    }

    @Override
    public User update(User user) {
        checkIdIsValid(user.getId());
        User originUser = storage.put(user.getId(), user.toBuilder().build());
        log.debug("Updated user: {} to {}", originUser, user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        checkIdIsValid(userId);
        storage.remove(userId);
        log.debug("Delete user with id: {}", userId);

    }

    private void checkIdIsValid(Long userId) {
        if (userId == null) {
            throw new IdNotSpecifiedException();
        }
        if (!storage.containsKey(userId)) {
            throw new NotFoundByIdException(userId);
        }
    }
}
