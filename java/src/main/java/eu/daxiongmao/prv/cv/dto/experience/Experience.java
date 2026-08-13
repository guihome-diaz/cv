package eu.daxiongmao.prv.cv.dto.experience;

import java.util.Optional;

public record Experience(
        Company company,
        String jobTitle,
        String tasks,
        DateRange dates,
        String jobSubTitle,
        String workingType,
        String workingTime,
        String jobType
) {
}
