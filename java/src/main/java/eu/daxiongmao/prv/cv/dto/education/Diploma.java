package eu.daxiongmao.prv.cv.dto.education;

import java.util.Optional;

public record Diploma(
        String name,
        String field,
        int year,
        Optional<String> degree,
        Optional<Integer> europeanLevel
) {
}
