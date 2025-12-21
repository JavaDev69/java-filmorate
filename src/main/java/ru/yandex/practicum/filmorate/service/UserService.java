package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    private final Map<Long, User> storage = HashMap.newHashMap(100);

    public User create(User user) {
        Long nextId = getNextId();
        user.setId(nextId);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        storage.put(nextId, user);
        log.debug("Added user: {}", user);
        return user;
    }

    public Collection<User> findAll() {
        return storage.values();
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new IdNotSpecifiedException();
        }
        if (!storage.containsKey(user.getId())) {
            throw new NotFoundByIdException(user.getId());
        }
        User originUser = storage.put(user.getId(), user);
        log.debug("Updated user: {} to {}", originUser, user);
        return user;
    }

    private Long getNextId() {
        long currentMaxId = storage.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        return ++currentMaxId;
    }
}
