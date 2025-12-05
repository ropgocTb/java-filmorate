package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(long filmId, long userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Film removeLike(long filmId, long userId) {
        return filmStorage.removeLike(filmId, userId);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void removeFilm(Film film) {
        filmStorage.removeFilm(film);
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public List<Film> getPopularFilms(int max) {
        return filmStorage.getPopularFilms(max);
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Genre getGenreById(long id) {
        return filmStorage.getGenreById(id);
    }

    public List<Rating> getMpa() {
        return filmStorage.getMpa();
    }

    public Rating getMpaById(long id) {
        return filmStorage.getMpaById(id);
    }
}
