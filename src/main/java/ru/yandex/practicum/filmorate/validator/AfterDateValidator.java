package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.AfterDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AfterDateValidator implements ConstraintValidator<AfterDate, LocalDate> {
    private LocalDate targetDate;

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return !value.isBefore(targetDate);
    }

    @Override
    public void initialize(AfterDate annotation) {
        String dateStr = annotation.value();
        String dateFormat = annotation.format();
        try {
            this.targetDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(dateFormat));
        } catch (Exception e) {
            throw new RuntimeException("Некорректный формат даты '" + dateStr + "' в аннотации @AfterDate. Используйте '"
                    + dateFormat + "'.");
        }

    }
}
