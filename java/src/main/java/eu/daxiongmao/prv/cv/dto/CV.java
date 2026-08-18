package eu.daxiongmao.prv.cv.dto;

import eu.daxiongmao.prv.cv.dto.education.Education;
import eu.daxiongmao.prv.cv.dto.experience.Experience;
import eu.daxiongmao.prv.cv.dto.global.Birth;
import eu.daxiongmao.prv.cv.dto.lang.Language;

import java.util.List;

public record CV(
        String lastName,
        String firstName,
        String gender,
        String phone,
        String phoneDisplay,
        String email,
        Birth birth,
        String nationality,
        String jobTitle,
        String personalStatement,
        String linkedIn,
        Address address,
        List<Education> education,
        List<Language> languages,
        List<Experience> experiences
) {
}
