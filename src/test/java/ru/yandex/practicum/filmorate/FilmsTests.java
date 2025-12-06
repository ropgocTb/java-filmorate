package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmsTests {
    FilmService service;
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final FilmStorage filmStorage = new InMemoryFilmStorage(userStorage);

    @BeforeEach
    public void init() {
        service = new FilmService(filmStorage);
    }

    @Test
    public void filmAddValidTest() {
        Film film = Film.builder().build();
        film.setName("Requiem for a Dream");
        film.setDescription("не надо употреблять, не надо, реально, ну зачем? (reason?)");
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(103);

        service.addFilm(film);
        assertEquals(1, service.getFilms().getFirst().getId(), "фильм не добавился");
    }

    @Test
    public void filmEmptyNameTest() {
        Film film = Film.builder().build();
        assertThrows(RuntimeException.class, () -> {
            Film film1 = service.addFilm(film);
        });
    }

    @Test
    public void filmMaxCharactersTest() {
        Film film = Film.builder().build();
        film.setName("Requiem for a Dream");
        film.setDescription("?".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(103);

        service.addFilm(film);
        assertEquals(1, service.getFilms().size(),
                "фильм с описанием в 200 символов не добавился");
    }

    @Test
    public void filmMaxCharactersPlusOneTest() {
        Film film = Film.builder().build();
        film.setName("Requiem for a Dream");
        film.setDescription("?".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(103);

        assertThrows(RuntimeException.class, () -> {
            service.addFilm(film);
        });
    }

    @Test
    public void filmReleaseDateTest() {
        Film film = Film.builder().build();
        film.setName("Requiem for a Dream");
        film.setDescription("?".repeat(201));
        film.setReleaseDate(LocalDate.now().plusDays(1));
        film.setDuration(103);

        assertThrows(RuntimeException.class, () -> {
            service.addFilm(film);
        });
    }

    @Test
    public void filmDurationFilmNegativeTest() {
        Film film = Film.builder().build();
        film.setName("Requiem for a Dream");
        film.setDescription("не надо употреблять, не надо, реально, ну зачем? (reason?)");
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(-103);

        assertThrows(RuntimeException.class, () -> {
            service.addFilm(film);
        });
    }

    @Test
    public void filmUpdateTest() {
        Film film = Film.builder().build();
        film.setName("Requiem for a Dream");
        film.setDescription("не надо употреблять, не надо, реально, ну зачем? (reason?)");
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(103);

        service.addFilm(film);

        film.setDuration(107);

        assertEquals(107, service.getFilms().getFirst().getDuration(), "фильм не " +
                "обновился");
    }

    //тест пустого запроса
    @Test
    public void emptyRequestTest() {
        assertThrows(NullPointerException.class, () -> {
            service.addFilm(null);
        });
    }
}
