package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.validator.AfterDateValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AfterDateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AfterDate {
    String message() default "Дата должна быть после {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value() default "28.12.1895";

    String format() default "dd.MM.yyyy";
}
