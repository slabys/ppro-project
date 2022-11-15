package cz.uhk.pproproject;

import cz.uhk.pproproject.model.RoleEnum;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DatabaseSeeder {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/seedDatabase")
    public String seed() {
        seedUsersTable();
        return "redirect:/";
    }

    private void seedUsersTable() {
        User databaseSeededUser = userRepo.findByEmail("admin@employerr.com");
        if(databaseSeededUser == null) {
            User admin = new User("admin@employerr.com","Arnoštek","Šimravý", RoleEnum.ADMIN);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode("123456789");
            admin.setPassword(encodedPassword);
            admin.setSalary(9000);
            admin.setActive(true);
            userRepo.save(admin);
            System.out.println("Seeded database with admin account admin@employer.com w pass 123456789");
        } else {
            System.out.println("No need to seed db, proceeding");
        }
    }
}
