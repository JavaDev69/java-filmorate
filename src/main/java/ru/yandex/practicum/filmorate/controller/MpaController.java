package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final RatingService ratingService;

    @GetMapping
    public Collection<Rating> findAll() {
        return ratingService.findAll();
    }

    @GetMapping("/{mpaId}")
    public Rating findById(@PathVariable long mpaId) {
        return ratingService.findById(mpaId);
    }
}
