package app.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class ThymeleafConfig {

    public static TemplateEngine templateEngine() {
        // TemplateEngine processes HTML templates and fills them with dynamic data for the web app
        TemplateEngine templateEngine = new TemplateEngine();

        //Like DNS for our localhost translating "/store" into the actual location in the project
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        //Only targets files in the resources/templates folder
        templateResolver.setPrefix("templates/"); // assuming templates are in resources/templates/

        //only process file that end with .html
        templateResolver.setSuffix(".html");

        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;

    }

}
