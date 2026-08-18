package eu.daxiongmao.prv.cv;

import eu.daxiongmao.prv.cv.dto.Address;
import eu.daxiongmao.prv.cv.dto.CV;
import eu.daxiongmao.prv.cv.dto.education.Education;
import eu.daxiongmao.prv.cv.dto.experience.ExperienceWithTasks;
import eu.daxiongmao.prv.cv.dto.global.Birth;
import eu.daxiongmao.prv.cv.dto.lang.Language;

import java.util.List;

/**
 * Original CV with advanced features
 */
public record CVAdvanced(
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
        List<ExperienceWithTasks> experiences
) {

    public CVAdvanced(CV cv) {
        this(
          cv.lastName(), cv.firstName(), cv.gender(),
                cv.phone(), cv.phoneDisplay(),
                cv.email(), cv.birth(),
                cv.nationality(),
                cv.jobTitle(),
                cv.personalStatement(),
                cv.linkedIn(),
                cv.address(),
                cv.education(),
                cv.languages(),
                ExperienceWithTasks.getExperiences(cv.experiences())
        );
    }

}
