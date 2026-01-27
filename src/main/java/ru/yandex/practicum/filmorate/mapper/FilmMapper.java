package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FilmMapper {

    @Mapping(target = "mpaId", qualifiedByName = "getMpaIdFromFilmDto", source = "mpa")
    Film map(FilmDto filmDto);

    @Mapping(target = "mpa", qualifiedByName = "getMpaFromFilm", source = "mpaId")
    @Mapping(target = "likes", source = "userLikeIds")
    FilmDto map(Film film);

    @Named("getMpaIdFromFilmDto")
    default Long getMpaIdFromFilmDto(Rating rating) {
        return rating == null ? null : rating.getId();
    }

    @Named("getMpaFromFilm")
    default Rating getMpaFromFilm(Long mpaId) {
        return mpaId == null ? null : new Rating(mpaId, null, null);
    }
}
