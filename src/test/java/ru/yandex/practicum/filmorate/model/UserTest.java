package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void whenAllFieldsValidThenNoValidationErrors() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setLogin("user123");
        user.setName("John Doe");
        user.setBirthday(LocalDate.parse("2000-01-01", ISO_LOCAL_DATE));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void whenEmailInvalidThenValidationError() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("user");
        user.setName("Name");
        user.setBirthday(LocalDate.parse("1990-01-01", ISO_LOCAL_DATE));


        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Количество ошибок валидации");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("must be a well-formed email address", violation.getMessage(), "Сообщение ошибки");
        assertEquals("email", violation.getPropertyPath().toString(), "Поле");
    }

    @Test
    public void whenEmailBlankThenValidationError() {
        User user = new User();
        user.setEmail("  ");
        user.setLogin("user");
        user.setName("Name");
        user.setBirthday(LocalDate.parse("1990-01-01", ISO_LOCAL_DATE));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(2, violations.size(), "Количество ошибок валидации");

        List<Map.Entry<String, String>> actualViolation = violations.stream()
                .map(e -> Map.entry(e.getPropertyPath().toString(), e.getMessage()))
                .toList();

        List<String> actualField = actualViolation.stream().map(Map.Entry::getKey).toList();
        List<String> actualValue = actualViolation.stream().map(Map.Entry::getValue).sorted().toList();

        List<String> expectedField = List.of("email", "email");
        List<String> expectedValue = Stream.of("must be a well-formed email address", "must not be blank").sorted().toList();

        assertIterableEquals(expectedField, actualField, "Поля");
        assertIterableEquals(expectedValue, actualValue, "Сообщения");
    }

    @Test
    public void whenLoginBlankThenValidationError() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("  ");
        user.setName("Name");
        user.setBirthday(LocalDate.parse("1990-01-01", ISO_LOCAL_DATE));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Количество ошибок валидации");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("must not be blank", violation.getMessage(), "Сообщение ошибки");
        assertEquals("login", violation.getPropertyPath().toString(), "Поле");
    }

    @Test
    public void whenBirthdayInFutureThenValidationError() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user");
        user.setName("Name");
        user.setBirthday(LocalDate.parse("2990-01-01", ISO_LOCAL_DATE));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Количество ошибок валидации");
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("must be a past date", violation.getMessage(), "Сообщение ошибки");
        assertEquals("birthday", violation.getPropertyPath().toString(), "Поле");
    }

    @Test
    public void whenBirthdayNullThenNoValidationError() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user");
        user.setName("Name");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}