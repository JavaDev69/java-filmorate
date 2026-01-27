package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("userDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ? , birthday = ? WHERE id = ?";
    private static final String ADD_FRIEND_TO_USER_QUERY = "INSERT INTO friendships(user_id, friend_id) VALUES (?, ?)";
    private static final String DELETE_FRIENDS_BY_USER_ID_QUERY = "DELETE FROM friendships WHERE user_id = ?";
    private static final String FIND_FRIENDS_BY_USER_ID_QUERY = "SELECT friend_id FROM friendships WHERE user_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate, RowMapper<User> mapper) {
        super(jdbcTemplate, mapper);
    }

    @Override
    public User create(User user) {
        long id = insert(INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay()));

        user.setId(id);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY).stream()
                .peek(user -> {
                    List<Long> friendsByUserId = getFriendsByUserId(user.getId());
                    user.getFriendIds().addAll(friendsByUserId);
                })
                .toList();
    }

    @Override
    public User findById(Long userId) {
        User user = findOne(FIND_BY_ID_QUERY, userId)
                .orElseThrow(() -> new NotFoundByIdException(userId));

        List<Long> friendsByUserId = getFriendsByUserId(user.getId());
        user.getFriendIds().addAll(friendsByUserId);
        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay()),
                user.getId());

        deleteFriendsByUserId(user.getId());
        addFriendToUser(user.getId(), user.getFriendIds());
        return user;
    }

    @Override
    public void delete(Long id) {
        delete(DELETE_BY_ID_QUERY, id);
    }

    public List<Long> getFriendsByUserId(long userId) {
        return jdbcTemplate.queryForList(FIND_FRIENDS_BY_USER_ID_QUERY, Long.class, userId);
    }


    private void addFriendToUser(long userId, Set<Long> friendIds) {
        friendIds.forEach(friendId -> update(ADD_FRIEND_TO_USER_QUERY, userId, friendId));
    }


    private void deleteFriendsByUserId(long userId) {
        delete(DELETE_FRIENDS_BY_USER_ID_QUERY, userId);
    }
}
