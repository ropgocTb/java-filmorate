package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmsTests {
    FilmController controller;
    @BeforeEach
    public void init() {
        controller = new FilmController();
    }

    @Test
    public void filmAddValidTest() {
        Film film = new Film();
        film.setName("Requiem for a Dream");
        film.setDescription("не надо употреблять, не надо, реально, ну зачем? (reason?)");
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(103);

        controller.addFilm(film);
        assertEquals(1, controller.getFilms().getFirst().getId(), "фильм не добавился");
    }

	@Test
    public void filmEmptyNameTest() {
        Film film = new Film();
        assertThrows(RuntimeException.class, () -> {
            Film film1 = controller.addFilm(film);
        });
    }

    @Test
    public void filmMaxCharactersTest() {
        Film film = new Film();
        film.setName("Requiem for a Dream");
        film.setDescription("?".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(103);

        controller.addFilm(film);
        assertEquals(1, controller.getFilms().size(), "фильм с описанием в 200 символов не добавился");
    }

    @Test
    public void filmMaxCharactersPlusOneTest() {
        Film film = new Film();
        film.setName("Requiem for a Dream");
        film.setDescription("?".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(103);

        assertThrows(RuntimeException.class, () -> {
            controller.addFilm(film);
        });
    }

    @Test
    public void filmReleaseDateTest() {
        Film film = new Film();
        film.setName("Requiem for a Dream");
        film.setDescription("?".repeat(201));
        film.setReleaseDate(LocalDate.now().plusDays(1));
        film.setDuration(103);

        assertThrows(RuntimeException.class, () -> {
            controller.addFilm(film);
        });
    }

    @Test
    public void filmDurationFilmNegativeTest() {
        Film film = new Film();
        film.setName("Requiem for a Dream");
        film.setDescription("не надо употреблять, не надо, реально, ну зачем? (reason?)");
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(-103);

        assertThrows(RuntimeException.class, () -> {
            controller.addFilm(film);
        });
    }

    @Test
    public void filmUpdateTest() {
        Film film = new Film();
        film.setName("Requiem for a Dream");
        film.setDescription("не надо употреблять, не надо, реально, ну зачем? (reason?)");
        film.setReleaseDate(LocalDate.of(2000, Month.DECEMBER, 1));
        film.setDuration(103);

        controller.addFilm(film);

        film.setDuration(107);

        assertEquals(107, controller.getFilms().getFirst().getDuration(), "фильм не " +
                "обновился");
    }

    //тест пустого запроса
    @Test
    public void emptyRequestTest() {
        assertThrows(NullPointerException.class, () -> {
            controller.addFilm(null);
        });
    }
}
