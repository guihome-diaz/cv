package eu.daxiongmao.prv.cv.business;

import eu.daxiongmao.prv.cv.dto.CV;
import eu.daxiongmao.prv.cv.dto.education.Diploma;
import eu.daxiongmao.prv.cv.dto.education.Education;
import eu.daxiongmao.prv.cv.dto.experience.Experience;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLReadFeature;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class CVLoader {

    private final ObjectMapper objectMapper;

    public CVLoader() {
        YAMLFactory yamlFactory = YAMLFactory.builder().build();
        this.objectMapper = new ObjectMapper(yamlFactory);
    }

    public CV load(Path yamlFile) {
        if (yamlFile == null || Files.notExists(yamlFile)) {
            throw new IllegalArgumentException("Given YAML file does not exist: " + yamlFile);
        }
        CV personalCV = objectMapper.readValue(yamlFile, CV.class);
        // Order experiences and diplomas
        personalCV.experiences().sort(experienceComparator);
        personalCV.education().sort(diplomaComparator);
        return personalCV;
    }

    /**
     * To sort the experiences from the current one (index 0) to the oldest (max index).
     */
    public Comparator<Experience> experienceComparator = (left, right) -> {
        if (left.dates() == null || left.dates().endTime() == null) { return -1; }
        if (right.dates() == null || right.dates().endTime() == null) { return 1; }
        // Both experiences are finished
        int cmp = Integer.compare(right.dates().endTime().year(), left.dates().endTime().year());
        if (cmp != 0) return cmp;
        return Integer.compare(right.dates().endTime().month(), left.dates().endTime().year());
    };

    /**
     * To sort the diplomas from the latest one (index 0) to the oldest (max index).
     */
    public Comparator<Education> diplomaComparator = (left, right) -> {
        return Integer.compare(right.diploma().year(), left.diploma().year());
    };

}
