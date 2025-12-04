package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
public class FilmService {
    @Autowired
    @Qualifier("FilmDbStorage")
    private FilmStorage filmStorage;
    @Autowired
    @Qualifier("UserDbStorage")
    private UserStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Film addLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (filmStorage instanceof InMemoryFilmStorage) {
            Set<Long> likes = film.getLikes();
            likes.add(user.getId());
            film.setLikes(likes);
            return filmStorage.updateFilm(film);
        } else if (filmStorage instanceof FilmDbStorage) {
            String sqlQuery = "insert into likes (film_id, user_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlQuery,
                    film.getId(),
                    user.getId());
        }

        return filmStorage.getFilmById(filmId);
    }

    public Film removeLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (filmStorage instanceof InMemoryFilmStorage) {
            Set<Long> likes = film.getLikes();
            likes.remove(user.getId());
            film.setLikes(likes);
            return filmStorage.updateFilm(film);
        } else if (filmStorage instanceof FilmDbStorage) {
            String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
            jdbcTemplate.update(sqlQuery, film.getId(), user.getId());
        }

        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getPopularFilms(int max) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparing(Film::getLikesCount)
                        .reversed())
                .limit(max)
                .collect(Collectors.toList());
    }

    public List<Genre> getGenres() {
        String sqlQuery = "select id, name from genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    public Genre getGenreById(long id) {
        String sqlQuery = "select id, name from genres where id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("no such genre id");
        }
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    public List<Rating> getMpa() {
        String sqlQuery = "select id, name from ratings";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    public Rating getMpaById(long id) {
        String sqlQuery = "select id, name from ratings where id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("no such rating id");
        }
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
