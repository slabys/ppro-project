package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.AuthProvider;
import java.security.Principal;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    // User activation
    @GetMapping("/activateUser/{token}")
    public String activateUser(@PathVariable String token){
        //TODO: check if UserActivationToken exists, check if user is not active, user registration form - set password all additional info -> post to /activateUser
        return "activateUserForm";
    }

    @PostMapping("/activateUser")
    public void activateUserPost(Model m, @ModelAttribute User userToActivate, HttpServletResponse resp) throws IOException {

        //TODO: add validation of data, update user in db (activate), add hashed password
        resp.sendRedirect("/mainPage");
    }

    @GetMapping("/registerUser")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup";
    }

    @PostMapping("/registerUser")
    public String processRegister(User user) {
        //add user to db
        //add email to db
        //send email to user
        //generate link for user activation -> registration with pass and additional info

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        //TODO set this for our auth
        user.setPassword(encodedPassword);

        userRepo.save(user);
        return "registerSuccessfull";
    }

    @GetMapping("/users")
    public String listUsers(Model model, Authentication auth) {
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("listUsers", listUsers);

        /* GET USER IN CONTROLLER
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        System.out.println(userDetails.getUser().getFirstName());
        */
        return "users";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
