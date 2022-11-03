package cz.uhk.pproproject.web;

import cz.uhk.pproproject.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UserController {

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
}
