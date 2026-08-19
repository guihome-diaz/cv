package eu.daxiongmao.prv.cv.dto.experience;

import eu.daxiongmao.prv.cv.dto.Address;

public record Company(
        String name,
        Address location,
        String linkedIn,
        String sector
) {
}
