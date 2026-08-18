package eu.daxiongmao.prv.cv.business;

import eu.daxiongmao.prv.cv.CVAdvanced;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Renders HTML from Thymeleaf template + CV model.
 * No PDF logic, no hardcoded layout
 */
public class ThymeleafRender {

    private final TemplateEngine templateEngine;

    public ThymeleafRender() {
        this.templateEngine = configureTemplateEngine();
    }

    private TemplateEngine configureTemplateEngine() {
        // 1. Template resolver (HTML files)
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);  // must be disabled for hot reload

        // 2. Message resolver (i18n properties files)
        ThymeleafMessageResolver messageResolver = new ThymeleafMessageResolver("i18n/messages");

        // 3. Assemble engine
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        engine.setMessageResolver(messageResolver);
        return engine;
    }

    /**
     * Renders CV to HTML string using Thymeleaf template.
     * @param cv the parsed CV model
     * @param templateFileName name of the template file name to use. Corresponding files must be:
     *                         * HTML: `src/resources/templates/_templateName_.html`
     *                         * CSS: `src/resources/static/css/_templateName_.css`
     * @param currentLocale current language
     * @return complete HTML document as String
     */
    public String htmlRendering(CVAdvanced cv, String templateFileName, Locale currentLocale) {
        Context context = new Context(currentLocale);

        // Computed variables for template convenience
        context.setVariable("cv", cv);
        context.setVariable("fullName", cv.firstName() + " " + cv.lastName());

        // Get CSS
        String cssContent = loadCssContent(templateFileName);
        context.setVariable("cssContent", cssContent);

        // Apply template
        return templateEngine.process(templateFileName, context);
    }

    /**
     * Render with inline CSS for PDF generation.
     */
    public String renderForPDF(CVAdvanced cv, String cssContent, String templateFileName, Locale currentLocale) {
        String html = htmlRendering(cv, templateFileName, currentLocale);

        // Inline CSS into <head> for PDF self-containment
        if (cssContent != null && !cssContent.isEmpty()) {
            html = html.replace(
                    "</head>",
                    "<style>\n" + cssContent + "\n</style>\n</head>"
            );
        }

        return html;
    }

    /**
     * Loads CSS file from classpath resources
     */
    private String loadCssContent(String templateName) {
        // Try as classpath resource (works in JAR or IDE)
        ClassLoader classLoader = ThymeleafRender.class.getClassLoader();
        String templateFile = "static/css/" + templateName + ".css";
        URL resourceUrl = classLoader.getResource(templateFile);

        if (resourceUrl != null) {
            try {
                Path path;
                if (resourceUrl.getProtocol().equals("jar")) {
                    // For JAR: copy to temp file or read as stream
                    try (var stream = resourceUrl.openStream()) {
                        return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                    }
                } else {
                    // Regular file system
                    path = Paths.get(resourceUrl.toURI());
                    return Files.readString(path, StandardCharsets.UTF_8);
                }
            } catch (IOException | URISyntaxException e) {
                throw new IllegalStateException("Failed to read CSS: " + templateFile, e);
            }
        }

        // Fallback: try filesystem path (for development)
        Path fsPath = Paths.get("src/main/resources/static/css", templateName + ".css");
        if (Files.exists(fsPath)) {
            try {
                return Files.readString(fsPath, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read CSS from filesystem: " + fsPath, e);
            }
        }
        throw new IllegalStateException("CSS not found: " + templateName + ".css");
    }
}
