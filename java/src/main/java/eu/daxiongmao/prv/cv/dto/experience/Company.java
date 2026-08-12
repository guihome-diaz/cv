package eu.daxiongmao.prv.cv.dto.experience;

import eu.daxiongmao.prv.cv.dto.Address;

import java.util.Optional;

public record Company(
        String name,
        Address location,
        Optional<String> linkedIn,
        Optional<String> sector
) {
}
