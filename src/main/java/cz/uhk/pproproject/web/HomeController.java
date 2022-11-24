package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.Project;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/")
    public String showHomepage(Model m, Authentication auth){
        if (auth != null) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            List<Project> userProjects = userDetails.getUser().getProjects();

            m.addAttribute("accessibleProjects", userProjects);
            System.out.println(userProjects);
        }
        return "landingPage";
    }

}
