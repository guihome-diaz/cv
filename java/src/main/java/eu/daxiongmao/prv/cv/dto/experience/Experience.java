package eu.daxiongmao.prv.cv.dto.experience;

import java.util.Optional;

public record Experience(
        Company company,
        String jobTitle,
        String tasks,
        DateRange dates,
        Optional<String> jobSubTitle,
        Optional<String> workingType,
        Optional<String> workingTime,
        Optional<String> jobType
) {
}
