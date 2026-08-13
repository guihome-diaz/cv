package eu.daxiongmao.prv.cv.dto.experience;


import java.util.Optional;

public record DateRange(
        YearMonth startTime,
        YearMonth endTime
) {
}
