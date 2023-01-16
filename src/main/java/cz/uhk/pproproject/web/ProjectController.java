package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.Project;
import cz.uhk.pproproject.model.Task;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.ProjectRepository;
import cz.uhk.pproproject.repository.TaskRepository;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProjectController(ProjectRepository projectRepository, UserRepository userRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping("/dashboard/project/manage/new")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String showProjectForm(Model m) {
        m.addAttribute("project", new Project());
        return "forms/project/projectForm";
    }

    @GetMapping("/dashboard/project/manage/{id}/createTask")
    public String modal(@PathVariable long id) {
        return "fragments/modal/createTask";
    }

    @PostMapping("/dashboard/project/manage/new")
    public String addProject(Project project, RedirectAttributes redirectAttrs, Authentication auth) {
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        project.setProjectOwner(null);

        projectRepository.save(project);

        user.addProject(project);
        userRepository.save(user);

        redirectAttrs.addFlashAttribute("info", "Project successfully created");
        return "redirect:/dashboard/project/list";
    }

    @PostMapping("/dashboard/project/newTask")
    public String addTaskToProject(HttpServletRequest request,long projectId, Task newTask, Authentication auth, RedirectAttributes redirectAttrs) {
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        Optional<Project> project = projectRepository.findById(projectId);
        if(project.isEmpty()){
            redirectAttrs.addFlashAttribute("error", "Project does not exists");
            if(request.getHeader("Referer").isEmpty()){
                return "redirect:/dashboard/project/list";
            }else{
                return "redirect:" + request.getHeader("Referer");
            }
        }
        if(!project.get().canUserEditProject(user)){
            redirectAttrs.addFlashAttribute("error", "You don't have permissions to do this action");
            throw new ResponseStatusException(FORBIDDEN);
        }
        newTask.setAssignedToProject(project.get());
        newTask.setCreatedBy(user);
        taskRepository.save(newTask);
        redirectAttrs.addFlashAttribute("info", "Task successfully created and assigned to project.");

        if(request.getHeader("Referer").isEmpty()){
            return "redirect:/dashboard/project/list";
        }else{
            return "redirect:" + request.getHeader("Referer");
        }
    }

    @GetMapping("/dashboard/project/manage/setOwner/{id}")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String showProjectOwnerForm(Model m, @PathVariable long id, RedirectAttributes redirectAttrs) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Project does not exist");
            throw new ResponseStatusException(NOT_FOUND, "Unable to find project");
        }
        List<User> users = userRepository.findAllHigherRoles();
        users.sort(Comparator.comparing(User::getRole));
        Collections.reverse(users);
        m.addAttribute("project", project.get());
        m.addAttribute("users", users);
        return "forms/project/projectOwnerForm";
    }

    @PostMapping("/dashboard/project/manage/setOwner")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String setProjectOwner(Project project, RedirectAttributes redirectAttrs) {
        Optional<Project> projectToEdit = projectRepository.findById(project.getId());
        if (projectToEdit.isPresent()) {
            User oldOwner = project.getProjectOwner();
            if (oldOwner != null) {
                oldOwner.removeFromProject(project);
                userRepository.save(oldOwner);
            }
            projectToEdit.get().setProjectOwner(project.getProjectOwner());
            projectRepository.save(projectToEdit.get());

            User user = project.getProjectOwner();
            if (!user.getProjects().contains(projectToEdit.get())) user.addProject(projectToEdit.get());
            userRepository.save(user);

            redirectAttrs.addFlashAttribute("info", "Project ownership assigned to " + project.getProjectOwner().getFullName() + " successfully");
        } else {
            redirectAttrs.addFlashAttribute("error", "Project ownership assign was not successful, because project does not exists");
        }
        return "redirect:/";
    }

    @GetMapping("/dashboard/project/manage/edit/{id}")
    public String editProject(Model m, @PathVariable long id, RedirectAttributes redirectAttrs, Authentication auth) {
        Optional<Project> project = projectRepository.findById(id);
        if (isProjectInvalidOrUserCannotEditProject(project, auth, redirectAttrs)) return "redirect:/";
        m.addAttribute("project", project.get());

        return "forms/project/projectEditForm";
    }

    @PostMapping("/dashboard/project/manage/edit")
    public String editProject(Project project, RedirectAttributes redirectAttrs, Authentication auth) {
        projectRepository.save(project);
        if (isProjectInvalidOrUserCannotEditProject(Optional.of(project), auth, redirectAttrs)) return "redirect:/";
        redirectAttrs.addFlashAttribute("info", "Edit of project with name " + project.getName() + " was successful");
            return "redirect:/dashboard/project/list/user";
    }

    @GetMapping("/dashboard/project/list")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String showProjectList(Model m) {
        List<Project> projects = projectRepository.findAll();
        m.addAttribute("projects", projects);
        return "project/projectList";
    }

    @GetMapping("/dashboard/project/list/user")
    public String showAccessibleProjectList(Model m, Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        Optional<User> updatedUser = userRepository.findById(user.getId());
        userDetails.updateUserDetails(updatedUser.get());
        m.addAttribute("projects", updatedUser.get().getProjects());
        return "project/projectList";
    }

    @GetMapping("/dashboard/project/detail/{id}")
    public String showProjectDetail(Model m, Authentication auth, @PathVariable long id, RedirectAttributes redirectAttrs) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Project does not exists");
            throw new ResponseStatusException(NOT_FOUND, "Project detail does not exists");
        }
        List<Task> projectTasks = taskRepository.findAllTaskByAssignedProject(project.get());
        List<Task> finishedTasks = taskRepository.findAllFinishedTasksByAssignedProject(project.get());

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();
        User lookupUser = userRepository.findByEmail(user.getEmail());

        if (!lookupUser.hasAccessToProject(project.get())) {
            redirectAttrs.addFlashAttribute("error", "You don't have permissions to view this project details");
            return "redirect:/";
        }

        for(Task task : projectTasks ) {
            task.setContent(task.getContent().replaceAll("<[^>]*>"," "));
        }
        for(Task task : finishedTasks){
            task.setContent(task.getContent().replaceAll("<[^>]*>"," "));
        }
        m.addAttribute("project", project.get());
        m.addAttribute("projectTasks", projectTasks);
        m.addAttribute("finishedTasks", finishedTasks);
        m.addAttribute("newTask", new Task());
        return "project/projectDetail";
    }

    @GetMapping("/dashboard/project/manage/addPeople/{id}")
    public String showAddPeopleToProjectForm(Model m, Authentication auth, @PathVariable long id, RedirectAttributes redirectAttrs) {
        Optional<Project> project = projectRepository.findById(id);
        if (isProjectInvalidOrUserCannotEditProject(project, auth, redirectAttrs)) return "redirect:/";

        List<User> employees = userRepository.findAllEmployeesNotInProject(project.get());
        List<User> managers = userRepository.findAllManagersNotInProject(project.get());
        List<User> higherRoles = userRepository.findAllHighRolesNotInProject(project.get());

        m.addAttribute("project", project.get());
        m.addAttribute("employees", employees);
        m.addAttribute("managers", managers);
        m.addAttribute("higherRoles", higherRoles);
        m.addAttribute("addPeople", true);
        return "forms/project/projectSetPeopleForm";
    }

    @PostMapping("/dashboard/project/manage/addPeople")
    public String addPeopleToProject(@RequestParam long id, RedirectAttributes redirectAttrs, @RequestParam List<Long> users, Authentication auth) {
        Optional<Project> project = projectRepository.findById(id);
        if (isProjectInvalidOrUserCannotEditProject(project, auth, redirectAttrs))
            return "redirect:/dashboard/project/list";

        for (Long userID : users) {
            Optional<User> optUser = userRepository.findById(userID);
            User selectedUser;
            if (optUser.isPresent()) {
                selectedUser = optUser.get();
                selectedUser.addProject(project.get());
                userRepository.save(selectedUser);
            }
        }
        redirectAttrs.addFlashAttribute("info", "Successfully added users to project called '" + project.get().getName() + "'");
            return "redirect:/";
    }

    @GetMapping("/dashboard/project/manage/removePeople/{id}")
    public String showRemovePeopleFromProjectForm(Model m, Authentication auth, @PathVariable long id, RedirectAttributes redirectAttrs) {
        Optional<Project> project = projectRepository.findById(id);
        if (isProjectInvalidOrUserCannotEditProject(project, auth, redirectAttrs)) return "redirect:/";

        List<User> employees = userRepository.findAllEmployeesInProject(project.get());
        List<User> managers = userRepository.findAllManagersInProject(project.get());
        List<User> higherRoles = userRepository.findAllHighRolesInProject(project.get());

        m.addAttribute("project", project.get());
        m.addAttribute("employees", employees);
        m.addAttribute("managers", managers);
        m.addAttribute("higherRoles", higherRoles);
        m.addAttribute("addPeople", false);
        return "forms/project/projectSetPeopleForm";
    }

    @PostMapping("/dashboard/project/manage/removePeople")
    public String removePeopleFromProject(@RequestParam long id, RedirectAttributes redirectAttrs, @RequestParam List<Long> users, Authentication auth) {
        Optional<Project> project = projectRepository.findById(id);
        if (isProjectInvalidOrUserCannotEditProject(project, auth, redirectAttrs)) return "redirect:/dashboard/project/list";
        for (Long userID : users) {
            Optional<User> optUser = userRepository.findById(userID);
            User selectedUser;
            if (optUser.isPresent()) {
                selectedUser = optUser.get();
                selectedUser.removeFromProject(project.get());
                userRepository.save(selectedUser);
            }
        }
        redirectAttrs.addFlashAttribute("info", "Successfully removed users from project called '" + project.get().getName() + "'");
            return "redirect:/";
    }

    //checks if project is invalid or if user can't edit project -> returns error/redirect
    public boolean isProjectInvalidOrUserCannotEditProject(Optional<Project> project, Authentication auth, RedirectAttributes redirectAttrs) {
        if (project.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Project does not exists");
            throw new ResponseStatusException(NOT_FOUND, "Project detail does not exists");
        }
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        if (!project.get().canUserEditProject(user)) {
            redirectAttrs.addFlashAttribute("error", "You don't have permissions to change this project");
            return true;
        }
        return false;
    }
}
