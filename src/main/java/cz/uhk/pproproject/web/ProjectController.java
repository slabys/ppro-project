package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.Project;
import cz.uhk.pproproject.model.RoleEnum;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.ProjectRepository;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class ProjectController {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard/project/manage/new")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String showProjectForm(Model m, Project project){
        m.addAttribute("project", new Project());
        return "forms/project/projectForm";
    }

    @PostMapping("/dashboard/project/manage/new")
    public String addProject(Project project, RedirectAttributes redirectAttrs, Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        project.setProjectOwner(user);

        projectRepository.save(project);
        //save project to owner
        user.addProject(project);
        userRepository.save(user);

        redirectAttrs.addFlashAttribute("info", "Project successfully created");
        return "redirect:/";
    }

    @GetMapping("/dashboard/project/manage/{id}/setOwner")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String showProjectOwnerForm(Model m, @PathVariable long id, RedirectAttributes redirectAttrs){
        Optional<Project> project = projectRepository.findById(id);
        if(project.isEmpty()){
            redirectAttrs.addFlashAttribute("error", "Project does not exist");
            throw new ResponseStatusException(NOT_FOUND, "Unable to find project");
        }
        List<User> users = userRepository.findAllHigherRoles();
        users.sort(Comparator.comparing(User::getRole));
        Collections.reverse(users);
        m.addAttribute("project", project.get());
        m.addAttribute("users",users);
        return "forms/project/projectOwnerForm";
    }

    @PostMapping("/dashboard/project/manage/setOwner")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String setProjectOwner(Project project, RedirectAttributes redirectAttrs, Authentication auth){
        Optional<Project> projectToEdit = projectRepository.findById(project.getId());
        if(projectToEdit.isPresent()){
            projectToEdit.get().setProjectOwner(project.getProjectOwner());
            projectRepository.save(projectToEdit.get());

            User user = project.getProjectOwner();
            if(!user.getProjects().contains(projectToEdit.get())) user.addProject(projectToEdit.get());
            userRepository.save(user);

            redirectAttrs.addFlashAttribute("info", "Project ownership assigned to " + project.getProjectOwner().getFullName() + " successfully");
        }else{
            redirectAttrs.addFlashAttribute("error", "Project ownership assign was not successful, because project does not exists");
        }
        return "redirect:/";
    }

    @GetMapping("/dashboard/project/manage/{id}/edit")
    public String editProject(Model m, @PathVariable long id, RedirectAttributes redirectAttrs,Authentication auth){
        Optional<Project> project = projectRepository.findById(id);
        if(project.isEmpty()){
            redirectAttrs.addFlashAttribute("error", "Project does not exists");
            throw new ResponseStatusException(NOT_FOUND, "Project does not exists");
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        if(!project.get().canUserEditProject(user)){
            redirectAttrs.addFlashAttribute("error", "You don't have permissions to change this project");
            return "redirect:/";
        }

        m.addAttribute("project", project.get());

        return "forms/project/projectEditForm";
    }

    @PostMapping("/dashboard/project/manage/edit")
    public String editProject(Project project, RedirectAttributes redirectAttrs, Authentication auth){
        projectRepository.save(project);
        redirectAttrs.addFlashAttribute("info","Edit of project with name " + project.getName() + " was successful");
        return "redirect:/";
    }

    @GetMapping("/dashboard/project/list")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String showProjectList(Model m){
        List<Project> projects = projectRepository.findAll();
        m.addAttribute("projects", projects);
        return "project/projectList";
    }
    @GetMapping("/dashboard/project/list/user")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String showAccessibleProjectList(Model m,Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        m.addAttribute("projects", user.getProjects());
        return "project/projectList";
    }

    @GetMapping("/dashboard/project/detail/{id}")
    public String showProjectDetail(Model m,Authentication auth,@PathVariable long id,RedirectAttributes redirectAttrs){
        Optional<Project> project = projectRepository.findById(id);
        if(project.isEmpty()){
            redirectAttrs.addFlashAttribute("error", "Project detail does not exists");
            throw new ResponseStatusException(NOT_FOUND, "Project detail does not exists");
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        if(!project.get().canUserEditProject(user) && !user.hasAccessToProject(project.get())){
            redirectAttrs.addFlashAttribute("error", "You don't have permissions to view this project details");
            return "redirect:/";
        }

        m.addAttribute("project", project.get());
        return "project/projectDetail";
    }
}
