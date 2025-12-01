package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Getter
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        Set<Long> likes = film.getLikes();
        likes.add(user.getId());
        film.setLikes(likes);
        return filmStorage.updateFilm(film);
    }

    public Film removeLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        Set<Long> likes = film.getLikes();
        likes.remove(user.getId());
        film.setLikes(likes);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int max) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes() != null ? film.getLikes().size() : 0)
                        .reversed())
                .limit(max)
                .collect(Collectors.toList());
    }
}
