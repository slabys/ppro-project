package cz.uhk.pproproject.web;

import cz.uhk.pproproject.model.*;
import cz.uhk.pproproject.repository.TaskRepository;
import cz.uhk.pproproject.repository.TaskTimeRepository;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.util.*;

@Controller
public class TimeController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskTimeRepository taskTimeRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/dashboard/timeOverview")
    public String showTimeOverview(Model m) {
        List<User> userRepo = userRepository.findAllActive();
        List<TimePerUser> timeOverview = new ArrayList<>();

        Map<String, Float> timeByTask = new HashMap<>();

        Duration totalTimeTracked;
        Duration taskTimeTotal;
        float totalTime = 0;
        for (User user : userRepo) {
            List<TaskTime> taskTimeRepoByUser = taskTimeRepository.findTaskTimeByUser(user);

            for (TaskTime taskTime : taskTimeRepoByUser) {
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

            timeOverview.add(
                    new TimePerUser(
                            user.getFullName(), totalTime, timeByTask
                    )
            );
            totalTime = 0;
            timeByTask = new HashMap<>();
        }

        m.addAttribute("timeOverview", timeOverview);
        return "timeOverview/timeOverview";
    }

}
