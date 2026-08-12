package eu.daxiongmao.prv.cv;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.daxiongmao.prv.cv.business.CVLoader;
import eu.daxiongmao.prv.cv.dto.CV;
import eu.daxiongmao.prv.cv.dto.education.Education;
import eu.daxiongmao.prv.cv.dto.experience.Experience;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

class CVLoaderTest {

    private final CVLoader cvLoader = new CVLoader();

    @Test
    void parseYaml() {
        Path testFile = Paths.get("src", "test", "resources", "2026.xiongmette.en.yaml");
        CV cv = cvLoader.load(testFile);
        Assertions.assertNotNull(cv);
        Assertions.assertEquals("QIN", cv.lastName());
        Assertions.assertEquals("Sisi", cv.firstName());
        Assertions.assertEquals("+352661831105", cv.phone());
        Assertions.assertEquals("sisi@qin-diaz.com", cv.email());
        Assertions.assertEquals("1984-01-17", cv.birthDate());
        Assertions.assertEquals("Chinese", cv.nationality());
        Assertions.assertTrue(cv.jobTitle().isPresent());
        Assertions.assertEquals("Inside Sales Specialist", cv.jobTitle().get());
        Assertions.assertTrue(cv.linkedIn().isPresent());
        Assertions.assertEquals("https://www.linkedin.com/in/qinsisi/?locale=en-US", cv.linkedIn().get());
        Assertions.assertNotNull(cv.address());
        Assertions.assertEquals("Luxembourg", cv.address().country());
        Assertions.assertEquals("Luxembourg", cv.address().city());

        Assertions.assertNotNull(cv.education());
        Assertions.assertEquals(5, cv.education().size());
        Education hr = cv.education().stream().filter(education -> "Master Human Resources Management".equals(education.diploma().name())).findAny().get();
        Assertions.assertEquals("Master Human Resources Management", hr.diploma().name());
        Assertions.assertEquals("Human Resources Management", hr.diploma().field());
        Assertions.assertEquals(2009, hr.diploma().year());
        Assertions.assertTrue(hr.diploma().degree().isPresent());
        Assertions.assertEquals("Master 2", hr.diploma().degree().get());
        Assertions.assertTrue(hr.diploma().europeanLevel().isPresent());
        Assertions.assertEquals(7, hr.diploma().europeanLevel().get());
        Assertions.assertEquals("IAE Brest - Écoles Universitaires de Management", hr.school().name());
        Assertions.assertNotNull(hr.school().location());

        Assertions.assertNotNull(cv.experiences());
        Assertions.assertEquals(14, cv.experiences().size());
        Experience ceratizitCSA = cv.experiences().stream().filter(experience -> "Sales Administrator".equals(experience.jobTitle())).findAny().get();
        Assertions.assertEquals("CERATIZIT", ceratizitCSA.company().name());
        Assertions.assertEquals("https://www.linkedin.com/company/ceratizit", ceratizitCSA.company().linkedIn().get());
        Assertions.assertEquals("Metallurgy industry", ceratizitCSA.company().sector().get());
        Assertions.assertEquals("Luxembourg", ceratizitCSA.company().location().country());
        Assertions.assertEquals("Mamer", ceratizitCSA.company().location().city());
        Assertions.assertEquals(2021, ceratizitCSA.dates().startTime().year());
        Assertions.assertEquals(3, ceratizitCSA.dates().startTime().month());
        Assertions.assertEquals(2025, ceratizitCSA.dates().endTime().get().year());
        Assertions.assertEquals(2, ceratizitCSA.dates().endTime().get().month());
        Assertions.assertEquals("on-site", ceratizitCSA.workingType().get());
        Assertions.assertEquals("full-time", ceratizitCSA.workingTime().get());
        Assertions.assertEquals("employee", ceratizitCSA.jobType().get());
        Assertions.assertEquals("Asia-Pacific Area (APAC)", ceratizitCSA.jobSubTitle().get());
        Assertions.assertNotNull(ceratizitCSA.tasks());
        Assertions.assertFalse(ceratizitCSA.tasks().trim().isBlank());
    }

}
