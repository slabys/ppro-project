package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.Task;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.TaskRepository;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;


@Controller
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard/tasks")
    public String showAllTasks(Model m) {
        List<Task> taskList = taskRepository.findAll();
        for (Task task : taskList) {
            task.setContent(task.getContent().replaceAll("\\<[^>]*>",""));
        }
        m.addAttribute("taskList", taskList);
        return "task/taskList";
    }

    @GetMapping("/dashboard/task/{id}")
    public String showAllTasks(Model m, @PathVariable long id, RedirectAttributes redirectAttrs) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Project does not exist");
            throw new ResponseStatusException(NOT_FOUND, "Unable to find project");
        }
        m.addAttribute("task", task.get());
        return "task/taskDetail";
    }

    @GetMapping("/dashboard/task/new")
    public String addNewTaskForm(Model m) {
        m.addAttribute("task", new Task());
        return "forms/task/newTask";
    }

    @PostMapping("/dashboard/task/new")
    public String addNewTask(Task task, RedirectAttributes redirectAttrs, Authentication auth) {
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        task.setCreatedBy(user);
        taskRepository.save(task);

        redirectAttrs.addFlashAttribute("info", "Task successfully created.");
        return "redirect:/dashboard/tasks";
    }
}
