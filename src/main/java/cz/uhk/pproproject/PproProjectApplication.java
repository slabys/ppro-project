package cz.uhk.pproproject;

import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.UserRepository;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;

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
