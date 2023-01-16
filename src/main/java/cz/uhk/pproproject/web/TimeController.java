package cz.uhk.pproproject.web;

import cz.uhk.pproproject.model.TaskTime;
import cz.uhk.pproproject.model.TimePerUser;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.TaskTimeRepository;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Controller
public class TimeController {
    private final UserRepository userRepository;

    private final TaskTimeRepository taskTimeRepository;

    public TimeController(UserRepository userRepository, TaskTimeRepository taskTimeRepository) {
        this.userRepository = userRepository;
        this.taskTimeRepository = taskTimeRepository;
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/dashboard/timeOverview")
    public String showTimeOverview(
            @RequestParam(required = false, name = "trackedTimeStart") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> trackedTimeStart,
            @RequestParam(required = false, name = "trackedTimeEnd") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> trackedTimeEnd,
            Model m) {

        List<User> allUsers = userRepository.findAllActive();
        List<TimePerUser> timeOverview = new ArrayList<>();

        Map<String, Float> timeByTask = new HashMap<>();

        Duration totalTimeTracked;
        Duration taskTimeTotal;
        float totalTime = 0;
        for (User user : allUsers){
            List<TaskTime> taskTimeRepoByUser = taskTimeRepository.findTaskTimeByUser(user);

            for (TaskTime taskTime : taskTimeRepoByUser) {
                if (trackedTimeStart.isEmpty() && trackedTimeEnd.isEmpty() ||
                        (
                                trackedTimeStart.isPresent() && trackedTimeEnd.isPresent() &&
                                        (taskTime.getStartTime().isAfter(trackedTimeStart.get().atStartOfDay()) &&
                                                (taskTime.getEndTime().isBefore(trackedTimeEnd.get().atStartOfDay())))
                        )
                        ||
                        (
                                trackedTimeStart.isPresent() && trackedTimeEnd.isEmpty() &&
                                        (taskTime.getStartTime().isAfter(trackedTimeStart.get().atStartOfDay()))
                        )
                        ||
                        (
                                trackedTimeEnd.isPresent() && trackedTimeStart.isEmpty() &&
                                                (taskTime.getEndTime().isBefore(trackedTimeEnd.get().atStartOfDay()))
                        )
                ) {
                    if (Objects.equals(user.getId(), taskTime.getUser().getId())) {
                        if (!timeByTask.containsKey(taskTime.getTask().getName())) {
                            taskTimeTotal = Duration.between(taskTime.getStartTime(), taskTime.getEndTime());
                            timeByTask.put(taskTime.getTask().getName(), taskTimeTotal.toHours() + ((float) taskTimeTotal.toMinutesPart() / 60));
                        } else {
                            float additionalTime = timeByTask.get(taskTime.getTask().getName());
                            Duration tmpTime = Duration.between(taskTime.getStartTime(), taskTime.getEndTime());
                            additionalTime += tmpTime.toHours() + ((float) tmpTime.toMinutesPart() / 60);
                            timeByTask.put(taskTime.getTask().getName(), additionalTime);
                        }
                        totalTimeTracked = Duration.between(taskTime.getStartTime(), taskTime.getEndTime());
                        totalTime += totalTimeTracked.toHours() + ((float) totalTimeTracked.toMinutesPart() / 60);
                    }
                }
            }

            timeOverview.add(
                    new TimePerUser(
                            user, totalTime, timeByTask
                    )
            );
            totalTime = 0;
            timeByTask = new HashMap<>();
        }

        m.addAttribute("timeOverview", timeOverview);
        return "timeOverview/timeOverview";
    }

}
