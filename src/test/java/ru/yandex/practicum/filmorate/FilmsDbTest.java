package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmsDbTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmService service;
    Film testFilm;
    User user;

    @BeforeEach
    public void init_each() {
        testFilm = Film.builder()
                .id(1L)
                .name("Начало")
                .description("дикаприю щурится")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .likesCount(0L)
                .mpa(Rating.builder()
                        .id(1)
                        .name("G")
                        .build())
                .genres(new ArrayList<>())
                .build();

        user = User.builder()
                .id(1)
                .name("Name")
                .login("Login")
                .email("123@")
                .birthday(LocalDate.now().minusDays(1))
                .build();
        filmDbStorage.addFilm(testFilm);
        userDbStorage.addUser(user);
    }

    @Test
    public void addFilmTest() {
        assertEquals(filmDbStorage.getFilmById(1), testFilm, "фильм не добавился");
    }

    @Test
    public void updateFilm() {
        testFilm.setDescription("Обновленное описание");
        filmDbStorage.updateFilm(testFilm);
        assertEquals("Обновленное описание", filmDbStorage.getFilmById(1).getDescription(),
                "описание не обновилось");
    }

    @Test
    public void removeFilm() {
        filmDbStorage.removeFilm(testFilm);

        assertThrows(NotFoundException.class, () -> {
            filmDbStorage.getFilmById(1);
        }, "фильм не удалился");
    }

    @Test
    public void getFilms() {
        assertEquals(1, filmDbStorage.getFilms().size(), "изначально только один тестовый фильм");

        Film testFilm1 = Film.builder()
                .id(2L)
                .name("Зеленый слоник")
                .description("о господи")
                .releaseDate(LocalDate.of(2010, 7, 16))
                .duration(148)
                .likesCount(0L)
                .mpa(Rating.builder()
                        .id(1)
                        .name("G")
                        .build())
                .genres(new ArrayList<>())
                .build();
        filmDbStorage.addFilm(testFilm1);

        assertEquals(2, filmDbStorage.getFilms().size(), "неверный размер списка фильмов " +
                "после изменения");
    }

    @Test
    public void addLikeTest() {
        service.addLike(testFilm.getId(), user.getId());

        assertEquals(1, filmDbStorage.getFilmById(testFilm.getId()).getLikesCount(),
                "не лайкнулось");
    }

    @Test
    public void removeLikeTest() {
        service.removeLike(testFilm.getId(), user.getId());

        assertEquals(0, filmDbStorage.getFilmById(testFilm.getId()).getLikesCount(),
                "не анлайкнулось");
    }
}
