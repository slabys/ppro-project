package cz.uhk.pproproject.web;

import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepo;
    @GetMapping("/")
    public String home(Model m){
        return "landingPage";
    }
}
