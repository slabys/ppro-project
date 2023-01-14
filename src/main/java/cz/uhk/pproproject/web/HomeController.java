package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.Project;
import cz.uhk.pproproject.model.Stats;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.ProjectRepository;
import cz.uhk.pproproject.repository.TaskRepository;
import cz.uhk.pproproject.repository.TaskTimeRepository;
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

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/")
    public String showHomepage(Model m, Authentication auth){
        if (auth != null) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            User authedUser = userRepo.findByEmail(userDetails.getUsername());
            userDetails.updateUserDetails(authedUser);

            List<Project> userProjects = userDetails.getUser().getProjects();

            if(authedUser.hasHigherRole()){
                List<Project> projects = projectRepository.findAll();
                int count = 0;
                int numOfUsersOnProject = 0;
                for (Project project:projects) {
                    numOfUsersOnProject += project.getUsersOnProject().size();
                    count++;
                }
                float averageOnProjects = numOfUsersOnProject / count;
                Stats stats = new Stats(userRepo.countAllEmployees(),userRepo.countAllManagers(), userRepo.countAllActive(), userRepo.avgSalary(),userRepo.sumSalary(),projectRepository.countAll(),projectRepository.countWithoutOwner(),taskRepository.count(),taskRepository.countAllNoncompletedTasks(),taskRepository.countAllFinishedTasksByAssignedProject(),averageOnProjects);

                m.addAttribute("stats",stats);
            }else{
                m.addAttribute("stats",null);
            }

            m.addAttribute("accessibleProjects", userProjects);
        }
        return "landingPage";
    }

}
