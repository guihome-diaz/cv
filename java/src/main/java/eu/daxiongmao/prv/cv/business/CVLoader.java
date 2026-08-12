package eu.daxiongmao.prv.cv.business;

import eu.daxiongmao.prv.cv.dto.CV;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLReadFeature;

import java.nio.file.Files;
import java.nio.file.Path;

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
        return objectMapper.readValue(yamlFile, CV.class);
    }

}
