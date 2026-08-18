package eu.daxiongmao.prv.cv;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

class ApplicationTest {

    @Test
    void standard_english() {
        Path testFile = Paths.get("src", "test", "resources", "2026.xiongmette.en.yaml");
        String template = "standard";
        Application app = new Application(testFile, template, Locale.ENGLISH);
        app.executeHtmlRendering();
    }

    @Test
    void standard_french() {
        Path testFile = Paths.get("src", "test", "resources", "2026.xiongmette.fr.yaml");
        String template = "standard";
        Application app = new Application(testFile, template, Locale.FRENCH);
        app.executeHtmlRendering();
    }


    @Test
    void betterCvTech_english() {
        Path testFile = Paths.get("src", "test", "resources", "2026.xiongmette.en.yaml");
        String template = "better-cv-tech";
        Application app = new Application(testFile, template, Locale.ENGLISH);
        app.executeHtmlRendering();
    }

}
