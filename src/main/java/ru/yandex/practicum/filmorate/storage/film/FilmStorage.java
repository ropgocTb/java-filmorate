package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void removeFilm(Film film);

    Film getFilmById(long id);

    List<Film> getFilms();

    List<Film> getPopularFilms(int max);

    Film addLike(long filmId, long userId);

    Film removeLike(long filmId, long userId);

    List<Genre> getGenres();

    Genre getGenreById(long id);

    List<Rating> getMpa();

    Rating getMpaById(long id);
}
