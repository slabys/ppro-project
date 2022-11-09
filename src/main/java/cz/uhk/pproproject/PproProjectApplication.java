package cz.uhk.pproproject;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.TemplateEngine;

@SpringBootApplication
public class PproProjectApplication {
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
    public static void main(String[] args) {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addDialect(new LayoutDialect());
        SpringApplication.run(PproProjectApplication.class, args);
    }

}
