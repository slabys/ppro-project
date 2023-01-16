package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.Comment;
import cz.uhk.pproproject.model.Task;
import cz.uhk.pproproject.model.TaskTime;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Controller
public class TaskController {
    private final TaskRepository taskRepository;

    private final TaskTimeRepository taskTimeRepository;

    private final CommentRepository commentRepository;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public TaskController(TaskRepository taskRepository, TaskTimeRepository taskTimeRepository, CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.taskTimeRepository = taskTimeRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/dashboard/tasks")
    public String showAllTasks(Model m) {
        List<Task> taskList = taskRepository.findAll();
        for (Task task : taskList) {
            task.setContent(task.getContent().replaceAll("<[^>]*>", ""));
        }
        m.addAttribute("taskList", taskList);
        return "task/taskList";
    }

    @GetMapping("/dashboard/task/detail/{id}")
    public String showTaskDetail(Authentication auth, Model m, @PathVariable long id, RedirectAttributes redirectAttrs) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User loggedUser = userDetails.getUser();
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Task does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find project");
        }
        if (!loggedUser.hasAccessToProject(task.get().getAssignedToProject())) {
            redirectAttrs.addFlashAttribute("info", "You don't have permissions to view this task");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission view this project");
        }
        List<Comment> comments = task.get().getTaskComments();
        comments.sort(Comparator.comparing(Comment::getCreatedAt));
        Collections.reverse(comments);

        List<TaskTime> taskTimeArrayList = taskTimeRepository.findAllUserTimes(loggedUser, task);
        List<TaskTime> thisMonthTime = new ArrayList<>();
        LocalDate today = LocalDate.now();
        float hoursWorked = 0;
        for (TaskTime taskTime : taskTimeArrayList) {
            if (today.getMonth() == taskTime.getStartTime().getMonth()) {
                thisMonthTime.add(taskTime);
            }
            Duration dur = Duration.between(taskTime.getStartTime(), taskTime.getEndTime());
            hoursWorked += dur.toHours() + ((float) dur.toMinutesPart() / 60);
        }
        hoursWorked = Float.parseFloat(df.format(hoursWorked));

        m.addAttribute("task", task.get());
        m.addAttribute("newComment", new Comment());
        m.addAttribute("newTaskTime", new TaskTime());
        m.addAttribute("user", loggedUser);
        m.addAttribute("hoursWorked", hoursWorked);
        m.addAttribute("timeList", thisMonthTime);
        m.addAttribute("comments", comments);
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

    @GetMapping("/dashboard/task/edit/{id}")
    public String editTaskForm(Authentication auth, Model m, @PathVariable long id, RedirectAttributes redirectAttrs) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User loggedUser = userDetails.getUser();
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Task does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find project");
        }
        if (!task.get().getAssignedToProject().canUserEditProject(loggedUser)) {
            redirectAttrs.addFlashAttribute("error", "You don't have permissions to view this task");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission view this project");
        }

        m.addAttribute("task", task.get());
        return "forms/task/editTask";
    }

    @PostMapping("/dashboard/task/edit")
    public String editTask(Task task, RedirectAttributes redirectAttrs) {

        Optional<Task> taskFromRepo = taskRepository.findById(task.getId());
        if (taskFromRepo.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Task does not exist");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find project");
        }
        taskFromRepo.get().setName(task.getName());
        taskFromRepo.get().setContent(task.getContent());
        taskRepository.save(taskFromRepo.get());

        redirectAttrs.addFlashAttribute("info", "Task successfully edited.");
        return "redirect:/dashboard/tasks";
    }

    @PostMapping("/dashboard/task/newComment")
    public String addComment(Comment comment, Long task, RedirectAttributes redirectAttributes, Authentication auth, HttpServletRequest request) {
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        Optional<Task> lookupTask = taskRepository.findById(task);
        if (lookupTask.isEmpty()) {
            redirectAttributes.addFlashAttribute("info", "Error occurred, task not found");
            if (request.getHeader("Referer").isEmpty()) {
                return "redirect:/dashboard/project/list/user";
            } else {
                return "redirect:" + request.getHeader("Referer");
            }
        }
        if (!user.hasAccessToProject(lookupTask.get().getAssignedToProject())) {
            redirectAttributes.addFlashAttribute("info", "You don't have permissions to comment this task");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to comment this task");
        }
        comment.setCreatedBy(user);
        commentRepository.save(comment);
        lookupTask.get().addComment(comment);
        taskRepository.save(lookupTask.get());

        if (request.getHeader("Referer").isEmpty()) {
            return "redirect:/dashboard/project/list/user";
        } else {
            return "redirect:" + request.getHeader("Referer");
        }
    }

    @PostMapping("/dashboard/task/addTime")
    public String addTimeToTask(Authentication auth, HttpServletRequest request, TaskTime taskTime, RedirectAttributes redirectAttributes, String startTimeString, String endTimeString) {
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        if (!user.hasAccessToProject(taskTime.getTask().getAssignedToProject())) {
            redirectAttributes.addFlashAttribute("info", "You don't have permissions to add time to this task");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to add time to this task");
        }
        taskTime.setStartTime(LocalDateTime.parse(startTimeString));
        taskTime.setEndTime(LocalDateTime.parse(endTimeString));
        Duration dur = Duration.between(taskTime.getStartTime(), taskTime.getEndTime());

        if ((dur.toMinutesPart() < 0) || dur.toHours() < 0 || dur.toSecondsPart() < 0) {
            redirectAttributes.addFlashAttribute("error", "Time cannot be negative");

            if (request.getHeader("Referer").isEmpty()) {
                return "redirect:/dashboard/project/list/user";
            } else {
                return "redirect:" + request.getHeader("Referer");
            }
        }

        taskTimeRepository.save(taskTime);
        redirectAttributes.addFlashAttribute("info", "Time on task successfully added.");

        if (request.getHeader("Referer").isEmpty()) {
            return "redirect:/dashboard/project/list/user";
        } else {
            return "redirect:" + request.getHeader("Referer");
        }
    }

    @PostMapping("/dashboard/task/finish/")
    public String finishTask(Task task, RedirectAttributes redirectAttributes, Authentication auth) {
        User user = ((CustomUserDetails) auth.getPrincipal()).getUser();
        if (!user.hasAccessToProject(task.getAssignedToProject())) {
            redirectAttributes.addFlashAttribute("info", "You don't have permissions to finish this task");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to finish this project");
        }
        task.setCompleted(true);
        task.setCompletedAt(new Date());
        task.setCompletedBy(user);

        taskRepository.save(task);
        redirectAttributes.addFlashAttribute("info", "Task marked as finished successfully");

        return "redirect:/dashboard/project/detail/" + task.getAssignedToProject().getId();
    }
}
