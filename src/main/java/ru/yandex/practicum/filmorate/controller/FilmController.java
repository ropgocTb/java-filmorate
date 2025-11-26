package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        try {
            validateFilm(film);
            film.setId(++id);
            films.put(id, film);
            log.info("Added: {}", film);
        } catch (ValidationException ex) {
            log.error("Validation Error: {}", ex.getMessage());
            throw new RuntimeException();
        }
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId()))
            throw new RuntimeException();
        try {
            validateFilm(film);
            films.put(film.getId(), film);
            log.info("Updated: {}", film);
        } catch (ValidationException ex) {
            log.error("Validation Error: {}", ex.getMessage());
            throw new RuntimeException();
        }
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty())
            throw new ValidationException("Название фильма не может быть пустым");
        if (film.getDescription().length() > 200)
            throw new ValidationException("Описание фильма не может быть больше 200 смволов");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("дата создания фильма не должна быть раньше 28 декабря 1895");
        if (film.getDuration() < 0)
            throw new ValidationException("длительность фильма не может быть отрицательной");
    }
}
