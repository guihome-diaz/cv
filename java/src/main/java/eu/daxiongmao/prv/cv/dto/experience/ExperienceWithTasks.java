package eu.daxiongmao.prv.cv.dto.experience;

import eu.daxiongmao.prv.cv.business.TaskParser;

import java.util.ArrayList;
import java.util.List;

public record ExperienceWithTasks(
        Company company,
        String jobTitle,
        String rawTasks,
        List<TaskParser.TaskItem> tasks,
        DateRange dates,
        String jobSubTitle,
        String workingType,
        String workingTime,
        String jobType
) {

    public ExperienceWithTasks(Experience exp) {
        this(
            exp.company(),
                exp.jobTitle(),
                exp.tasks(),
                TaskParser.parse(exp.tasks()),
                exp.dates(),
                exp.jobSubTitle(),
                exp.workingType(),
                exp.workingTime(),
                exp.jobType()
            );
    }

    public static List<ExperienceWithTasks> getExperiences(List<Experience> experiences) {
        List<ExperienceWithTasks> result = new ArrayList<>();
        for (Experience exp : experiences) {
            result.add(new ExperienceWithTasks(exp));
        }
        return result;
    }

}
