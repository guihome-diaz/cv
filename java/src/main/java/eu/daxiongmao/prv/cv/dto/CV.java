package eu.daxiongmao.prv.cv.dto;

import eu.daxiongmao.prv.cv.dto.education.Education;
import eu.daxiongmao.prv.cv.dto.experience.Experience;
import eu.daxiongmao.prv.cv.dto.lang.Language;

import java.util.List;
import java.util.Optional;

public record CV(
        String lastName,
        String firstName,
        String phone,
        String email,
        String birthDate,
        String nationality,
        Optional<String> jobTitle,
        Optional<String> linkedIn,
        Address address,
        List<Education> education,
        List<Language> languages,
        List<Experience> experiences
) {
}
