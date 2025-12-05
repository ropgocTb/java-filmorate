package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("InMemoryFilmStorage")
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final UserStorage userStorage;

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Film added: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Film updated: {}", film);
        return film;
    }

    @Override
    public void removeFilm(Film film) {
        getFilmById(film.getId());
        films.remove(film.getId());
        log.info("Film deleted: {}", film);
    }

    @Override
    public Film getFilmById(long id) {
        if (films.containsKey(id))
            return films.get(id);
        throw new NotFoundException("Фильм с id " + id + " не найден");
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getPopularFilms(int max) {
        return getFilms().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes() != null ? film.getLikes().size() : 0)
                        .reversed())
                .limit(max)
                .collect(Collectors.toList());
    }

    @Override
    public Film addLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        Set<Long> likes = film.getLikes();
        likes.add(user.getId());
        film.setLikes(likes);
        return updateFilm(film);
    }

    @Override
    public Film removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        Set<Long> likes = film.getLikes();
        likes.remove(user.getId());
        film.setLikes(likes);
        return updateFilm(film);
    }

    @Override
    public List<Genre> getGenres() {
        return List.of();
    }

    @Override
    public Genre getGenreById(long id) {
        return null;
    }

    @Override
    public List<Rating> getMpa() {
        return List.of();
    }

    @Override
    public Rating getMpaById(long id) {
        return null;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty())
            throw new ValidationException("Название фильма не может быть пустым");
        if (film.getDescription() == null || film.getDescription().length() > 200)
            throw new ValidationException("Описание фильма не может быть больше 200 смволов");
        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("дата создания фильма не должна быть раньше 28 декабря 1895");
        if (film.getDuration() <= 0)
            throw new ValidationException("длительность фильма не может быть отрицательной");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
