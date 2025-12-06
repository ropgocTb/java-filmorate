package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;

    @GetMapping
    public List<Film> getFilms() {
        return service.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return service.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10", required = false) final int count) {
        return service.getPopularFilms(count);
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return service.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return service.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable final long id, @PathVariable final long userId) {
        return service.addLike(id, userId);
    }

    @DeleteMapping
    public void removeFilm(@RequestBody Film film) {
        service.removeFilm(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable final long id, @PathVariable final long userId) {
        return service.removeLike(id, userId);
    }
}
