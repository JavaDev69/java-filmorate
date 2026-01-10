package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class User {
    private Long id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    @Builder.Default
    @Builder.ObtainVia(method = "copyFriends")
    private Set<Long> friendIds = new HashSet<>();

    private Set<Long> copyFriends() {
        return new HashSet<>(friendIds);
    }
}
