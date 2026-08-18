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
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Application {

    public static final List<Locale> SUPPORTED_LOCALES = List.of(Locale.ENGLISH, Locale.FRENCH);

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    /** Personal data to include, in YAML format */
    private Path data;

    /** Name of the Thymeleaf template to use */
    private String template;

    /** Current language */
    private Locale lang;

    public static void main(String[] args) throws Exception {
        // Get template and data
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }
        // Compute language
        Optional<String> langArg = getLanguageArg(args);
        Locale currentLang = DEFAULT_LOCALE;
        if (langArg.isPresent()) {
            currentLang = computeLocale(langArg.get());
        }

        // Init app
        Application app = new Application(args[0], args[1], currentLang);

        // Execute rendering
        app.executeHtmlRendering();

        // Successful execution
        System.exit(0);
    }

    public Application(String sourceFile, String template, Locale language) {
        this(Paths.get(sourceFile), template, language);
    }

    public Application(Path data, String template, Locale language) {
        if (data == null || Files.notExists(data)) {
            throw new IllegalArgumentException("Missing source data, it must be a YAML file. Invalid source file: " + data);
        }
        this.data = data;
        this.template = template;
        this.lang = language;
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
        String html = render.htmlRendering(personalCV, this.template, this.lang);

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

    private static Optional<String> getLanguageArg(String[] args) {
        Optional<String> language = Optional.empty();
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--lang") || arg.equals("-l")) {
                if (i + 1 < args.length) {
                    language = Optional.of(args[i + 1]);
                    break;
                } else {
                    System.err.println("Error: " + arg + " requires a value");
                    printUsage();
                    System.exit(1);
                }
            } else if (arg.startsWith("--lang=")) {
                language = Optional.of(arg.substring("--lang=".length()));
            } else if (arg.startsWith("-l")) {
                // Handle -lfr or -l fr
                if (arg.length() > 2) {
                    language = Optional.of(arg.substring(2));
                } else if (i + 1 < args.length) {
                    language = Optional.of(args[i + 1]);
                    i++;
                }
            }
        }
        return language;
    }

    private static Locale computeLocale(String language) {
        try {
            Locale requestedLocale = Locale.of(language);
            if (!SUPPORTED_LOCALES.contains(requestedLocale)) {
                System.err.println("Requested language does not belong to the list of supported language: " + SUPPORTED_LOCALES);
                System.err.println("Use default language instead: " + DEFAULT_LOCALE);
                return DEFAULT_LOCALE;
            }
            return requestedLocale;
        } catch (Exception e) {
            System.err.println("Request language '" + language + "' is not a valid java Locale");
            printUsage();
            System.exit(2);
            // Unreachable code, only here for compilation purposes
            return DEFAULT_LOCALE;
        }
    }

    private static void printUsage() {
        System.out.printf("""
                CV Generator - Usage:
                  java -jar cv-generator.jar <cv.yaml> <templateName> [options]
                
                Options:
                  --lang, -l <code> Output language (fr, en, ..)
                     supported languages are: %s
                
                Examples:
                  java -jar cv-generator.jar cv_sisi_qin.yaml standard-template
                  java -jar cv-generator.jar cv.yaml standard-template --lang=en
                  java -jar cv-generator.jar cv.yaml standard-template -l de
                %n""", SUPPORTED_LOCALES);
    }
}
