package eu.daxiongmao.prv.cv.business;

import eu.daxiongmao.prv.cv.dto.CV;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

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
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false);  // must be disabled for hot reload

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    /**
     * Renders CV to HTML string using Thymeleaf template.
     * @param cv the parsed CV model
     * @param templateFileName name of the HTML template file name to use. It must be in `src/resources/templates` directory
     * @return complete HTML document as String
     */
    public String htmlRendering(CV cv, String templateFileName) {
        Context context = new Context();

        // Computed variables for template convenience
        context.setVariable("cv", cv);
        context.setVariable("hasLinkedIn", cv.linkedIn().isPresent());
        context.setVariable("fullName", cv.firstName() + " " + cv.lastName());

        // Apply template
        return templateEngine.process(templateFileName, context);
    }

    /**
     * Render with inline CSS for PDF generation.
     */
    public String renderForPDF(CV cv, String cssContent, String templateFileName) {
        String html = htmlRendering(cv, templateFileName);

        // Inline CSS into <head> for PDF self-containment
        if (cssContent != null && !cssContent.isEmpty()) {
            html = html.replace(
                    "</head>",
                    "<style>\n" + cssContent + "\n</style>\n</head>"
            );
        }

        return html;
    }

}
