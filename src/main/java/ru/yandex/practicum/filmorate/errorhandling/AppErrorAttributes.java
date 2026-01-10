package ru.yandex.practicum.filmorate.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import ru.yandex.practicum.filmorate.exception.IdNotSpecifiedException;
import ru.yandex.practicum.filmorate.exception.NotFoundByIdException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AppErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        Throwable error = getError(webRequest);
        log.error("Произошла ошибка", error);
        if (error instanceof MethodArgumentNotValidException er) {
            List<String> message = er.getAllErrors().stream()
                    .map(e -> {
                        if (e instanceof FieldError fe) {
                            return String.join(" : ", fe.getField(), fe.getDefaultMessage());
                        } else {
                            return e.getDefaultMessage();
                        }
                    })
                    .toList();
            errorAttributes.put("messages", message);
        } else if (error instanceof IdNotSpecifiedException || error instanceof NotFoundByIdException) {
            errorAttributes.put("messages", error.getMessage());
        }
        return errorAttributes;
    }
}
