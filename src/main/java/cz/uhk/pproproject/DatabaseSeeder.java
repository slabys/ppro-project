package cz.uhk.pproproject;

import cz.uhk.pproproject.model.Contact;
import cz.uhk.pproproject.model.RoleEnum;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.ContactRepository;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DatabaseSeeder {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/seedDatabase")
    public String seed(RedirectAttributes redirectAttributes) {
        seedUsersTable();
        redirectAttributes.addFlashAttribute("info","Admin create script is done!");
        return "redirect:/";
    }

    private void seedUsersTable() {
        User databaseSeededUser = userRepo.findByEmail("admin@employerr.com");
        if(databaseSeededUser == null) {
            User admin = new User("admin@employerr.com","Arnoštek","Šimravý", RoleEnum.ADMIN);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode("123456789");
            admin.setRegistrationEmail("admin@employerr.com");
            admin.setPassword(encodedPassword);
            admin.setSalary(9000);
            admin.setActive(true);
            Contact contact = new Contact();
            contactRepository.save(contact);
            admin.setContact(contact);
            userRepo.save(admin);
        } else {
            System.out.println("No need to seed db, proceeding");
        }
    }
}
