package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
@Builder
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Set<Long> likes = new HashSet<>();
    private Long likesCount;
    private Rating mpa;
    @JsonProperty("genres")
    private List<Genre> genres;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("title", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rating_id", mpa.getId());
        return values;
    }
}
