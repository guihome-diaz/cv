package eu.daxiongmao.prv.cv;

import eu.daxiongmao.prv.cv.business.CVLoader;
import eu.daxiongmao.prv.cv.business.TaskParser;
import eu.daxiongmao.prv.cv.dto.CV;
import eu.daxiongmao.prv.cv.dto.experience.Experience;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class TaskParserTest {

    private final CVLoader cvLoader = new CVLoader();
    private final TaskParser taskParser = new TaskParser();

    @Test
    void experienceWithTasks() {
        Path testFile = Paths.get("src", "test", "resources", "2026.xiongmette.en.yaml");
        CV cv = cvLoader.load(testFile);
        Experience futureCitizen = cv.experiences().stream().filter(experience -> "Office Administrator".equals(experience.jobTitle())).findAny().get();

        List<TaskParser.TaskItem> tasks = taskParser.parse(futureCitizen.tasks());
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(4, tasks.size());
        Assertions.assertEquals("Retaining all prior responsibilities, took on the following:", tasks.getFirst().text());
        Assertions.assertTrue(tasks.getFirst().subItems().isEmpty());
        Assertions.assertEquals("Managed social media platforms (Facebook, LinkedIn)", tasks.get(1).text());
        Assertions.assertTrue(tasks.get(1).subItems().isEmpty());
        Assertions.assertEquals("Filed and updated RCS registrations", tasks.get(2).text());
        Assertions.assertTrue(tasks.get(2).subItems().isEmpty());
        Assertions.assertEquals("Managed company intellectual property registration and renewals (EUIPO, EPO)", tasks.get(3).text());
        Assertions.assertTrue(tasks.get(3).subItems().isEmpty());
    }

    @Test
    void experienceWithSubTasks() {
        Path testFile = Paths.get("src", "test", "resources", "2026.xiongmette.en.yaml");
        CV cv = cvLoader.load(testFile);
        Experience ceratizitCSA = cv.experiences().stream().filter(experience -> "Sales Administrator".equals(experience.jobTitle())).findAny().get();

        List<TaskParser.TaskItem> tasks = taskParser.parse(ceratizitCSA.tasks());
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(2, tasks.size());

        TaskParser.TaskItem customRelations = tasks.getFirst();
        Assertions.assertEquals("Customer relations", customRelations.text());
        Assertions.assertEquals(3, customRelations.subItems().size());
        Assertions.assertEquals("Serve as dedicated point of contact for customers", customRelations.subItems().getFirst());
        Assertions.assertEquals("Manage the quote-to-cash process", customRelations.subItems().get(1));
        Assertions.assertEquals("Handle claims and post-sales activities for existing customers", customRelations.subItems().get(2));

        TaskParser.TaskItem internalCoordination = tasks.get(1);
        Assertions.assertEquals("Internal coordination", internalCoordination.text());
        Assertions.assertEquals(2, internalCoordination.subItems().size());
        Assertions.assertEquals("Coordinate between customers and internal departments (design, production, supply chain, quality)", internalCoordination.subItems().getFirst());
        Assertions.assertEquals("Serve as liaison between the Luxembourg factory and Asian partner or affiliate factories (orders, production follow-up, intercompany pricing, and quarterly reporting)", internalCoordination.subItems().get(1));
    }
}
