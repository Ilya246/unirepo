package ru.ssau.tk._AMEBA_._PESEZ_.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class HtmlTemplateLoader {

    public static String loadTemplate(String templateName) throws IOException {
        return loadTemplate(templateName, Map.of());
    }

    public static String loadTemplate(String templateName, Map<String, String> variables) throws IOException {
        ClassPathResource resource = new ClassPathResource("static/templates/" + templateName);
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // Замена переменных в шаблоне
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return content;
    }
}