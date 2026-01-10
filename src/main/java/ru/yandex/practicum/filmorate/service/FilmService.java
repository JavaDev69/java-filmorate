package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;

@RequiredArgsConstructor
@Service
public class FilmService {
    Comparator<Film> popularComparator = Comparator.comparingInt(o -> o.getUserLikeIds().size());
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long filmId) {
        return filmStorage.findById(filmId);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void delete(long filmId) {
        filmStorage.delete(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        User user = userService.findById(userId);
        Film film = filmStorage.findById(filmId);
        film.getUserLikeIds().add(user.getId());
        update(film);
    }

    public void deleteLike(Long filmId, Long userId) {
        User user = userService.findById(userId);
        Film film = filmStorage.findById(filmId);
        film.getUserLikeIds().remove(user.getId());
        update(film);

    }

    public Collection<Film> findPopular(int count) {
        return findAll()
                .stream()
                .sorted(popularComparator.reversed())
                .limit(count)
                .toList();
    }
}
