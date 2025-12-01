package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectRequest(final ValidationException e) {
        log.error("Validation error: {}", e.getMessage());
        return Map.of(
                "error", "Ошибка валидации",
                "errorMessage", e.getMessage()
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log.error("Not_Found error: {}", e.getMessage());
        return Map.of(
                "error", "Такого элемента нет",
                "errorMessage", e.getMessage()
        );
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRuntimeError(final RuntimeException e) {
        log.error("Runtime error: {}", e.getMessage());
        return Map.of(
                "error", "Ошибка сервера",
                "errorMessage", e.getMessage()
        );
    }
}
