package eu.daxiongmao.prv.cv;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

class ApplicationTest {

    @Test
    void applicationTest() {
        Path testFile = Paths.get("src", "test", "resources", "2026.xiongmette.en.yaml");
        String template = "standard";
        Application app = new Application(testFile, template);
        app.executeHtmlRendering();
    }


}
