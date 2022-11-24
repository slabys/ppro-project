package cz.uhk.pproproject.web;

import cz.uhk.pproproject.model.Project;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectController {
    @GetMapping("/manage/project")
    public String showProjectForm(Model m){
        return "projectForm";
    }
}
