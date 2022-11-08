package cz.uhk.pproproject.web;

import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;
    // User login
    @GetMapping("/login")
    public String loginGet(){
        return "login";
    }

    @PostMapping("/login")
    public void loginPost(Model m, @ModelAttribute User loginPerson, HttpServletResponse resp) throws IOException {

        //TODO: add verification and redirect to correct page
        resp.sendRedirect("/login");
    }

    // User activation
    @GetMapping("/activateUser/{id}")
    public String activateUser(@PathVariable Long id){
        return "activateUserForm";
    }

    @PostMapping("/activateUser")
    public void activateUserPost(Model m, @ModelAttribute User userToActivate, HttpServletResponse resp) throws IOException {

        //TODO: add verification, insert user into db and redirect
        resp.sendRedirect("/mainPage");
    }

    @GetMapping("/registerUser")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup";
    }

    @PostMapping("/registerUser")
    public String processRegister(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        //TODO set this for our auth
        user.setPassword(encodedPassword);

        userRepo.save(user);

        return "registerSuccessfull";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("listUsers", listUsers);

        return "users";
    }
}
