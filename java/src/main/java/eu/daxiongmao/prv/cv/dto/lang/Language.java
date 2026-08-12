package eu.daxiongmao.prv.cv.dto.lang;

import java.util.Optional;

public record Language(
        String language,
        boolean motherTongue,
        String level,
        Optional<String> description
) {
}
