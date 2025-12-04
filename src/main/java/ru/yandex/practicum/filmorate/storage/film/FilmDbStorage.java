package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Primary
@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        long filmId = resultSet.getLong("id");
        return Film.builder()
                .id(filmId)
                .name(resultSet.getString("title"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .description(resultSet.getString("description"))
                .duration(resultSet.getLong("duration"))
                .mpa(getFilmRating(resultSet.getLong("rating_id")))
                .genres(getFilmGenres(filmId))
                .likesCount(getFilmLikesCount(filmId))
                .build();
    }

    private Long getFilmLikesCount(long filmId) {
        String sqlQuery = "select COUNT(film_id) as likes_count from likes where film_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToLikesCount, filmId);
    }

    private Long mapRowToLikesCount(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("likes_count");
    }

    private List<Genre> getFilmGenres(long filmId) {
        String sqlQuery = "select g.id, g.name from film_genres as fg JOIN genres as g ON fg.genre_id = g.id " +
                "where fg.film_id = ? ORDER BY g.id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    private Rating getFilmRating(long ratingId) {
        String sqlQuery = "select id, name from ratings where id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, ratingId);
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();

        if (film.getGenres() != null) {
            String sqlQuery = "insert into film_genres (film_id, genre_id) " +
                    "values (?, ?)";
            Set<Genre> genreSet = new HashSet<>(film.getGenres());
            for (Genre genre : genreSet) {
                jdbcTemplate.update(sqlQuery,
                        filmId,
                        genre.getId());
            }
        }
        return getFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        String sqlQuery = "update films set " +
                "title = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return getFilmById(film.getId());
    }

    @Override
    public void removeFilm(Film film) {
        String sqlQuery = "delete from films where id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public Film getFilmById(long id) {
        try {
            String sqlQuery = "select id, title, description, release_date, duration, rating_id " +
                    "from films where id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "select id, title, description, release_date, duration, rating_id " +
                "from films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
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
        if (film.getMpa().getId() < 1 || film.getMpa().getId() > 5)
            throw new NotFoundException("такого рейтинга не предусмотрено");
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                if (genre.getId() < 1 || genre.getId() > 6) {
                    throw new NotFoundException("такого жанра не предусмотрено");
                }
            }
        }
    }
}
