package eu.daxiongmao.prv.cv;

import eu.daxiongmao.prv.cv.business.CVLoader;
import eu.daxiongmao.prv.cv.business.ThymeleafRender;
import eu.daxiongmao.prv.cv.dto.CV;

import javax.crypto.spec.PSource;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {

    /** Personal data to include, in YAML format */
    private Path data;

    /** Name of the Thymeleaf template to use */
    private String template;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }
        // Init app
        Application app = new Application(args[0], args[1]);

        // Execute rendering
        app.executeHtmlRendering();

        // Successful execution
        System.exit(0);
    }

    public Application(String sourceFile, String template) {
        this(Paths.get(sourceFile), template);
    }

    public Application(Path data, String template) {
        if (data == null || Files.notExists(data)) {
            throw new IllegalArgumentException("Missing source data, it must be a YAML file. Invalid source file: " + data);
        }
        this.data = data;
        this.template = template;
    }

    /**
     * To generate HTML CV
     * @return path to HTML file
     */
    public String executeHtmlRendering() {
        // 1. PARSE YAML
        CVLoader parser = new CVLoader();
        CV personalCV = parser.load(this.data);
        System.out.println("Parsed: " + personalCV.firstName() + " " + personalCV.lastName());

        // 2. Render HTML
        ThymeleafRender render = new ThymeleafRender();
        String html = render.htmlRendering(personalCV, this.template);

        // Debug: save HTML
        try {
            Path outputDir = Paths.get("target", "html");
            Files.createDirectories(outputDir);
            Path htmlPath = outputDir.resolve("htmlRendering.html");
            Files.writeString(htmlPath, html);
            System.out.println("HTML file: " + htmlPath);
            return htmlPath.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save HTML file",e );
        }
    }

    private static void printUsage() {
        System.out.println("""
            CV Generator - Usage:
              java -jar cv-generator.jar <cv.yaml> <templateName> [options]
            
            Options:
              --lang, -l <code>   Output language (fr, en, de, es, ...)
                                  Auto-detected from CV nationality if not specified
            
            Examples:
              java -jar cv-generator.jar cv_sisi_qin.yaml standard-template
              java -jar cv-generator.jar cv.yaml standard-template --lang=en
              java -jar cv-generator.jar cv.yaml standard-template -l de
            """);
    }
}
