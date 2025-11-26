package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    @JsonProperty("duration")
    private Duration duration;

    public long getDuration() {
        return duration.toMinutes();
    }

    public void setDuration(long duration) {
        this.duration = Duration.ofMinutes(duration);
    }
}
