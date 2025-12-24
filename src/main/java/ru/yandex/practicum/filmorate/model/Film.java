package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.annotation.AfterDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @AfterDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    @Builder.Default
    @Builder.ObtainVia(method = "copyLikes")
    private Set<Long> userLikeIds = new HashSet<>();

    private Set<Long> copyLikes() {
        return new HashSet<>(userLikeIds);
    }
}
