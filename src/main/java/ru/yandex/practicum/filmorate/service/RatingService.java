package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class RatingService {
    @Qualifier("ratingDbStorage")
    private final RatingStorage ratingStorage;

    public Collection<Rating> findAll() {
        return ratingStorage.findAll();
    }

    public Rating findById(Long ratingId) {
        return ratingStorage.findById(ratingId);
    }

}
