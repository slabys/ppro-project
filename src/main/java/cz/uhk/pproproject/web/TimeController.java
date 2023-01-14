package cz.uhk.pproproject.web;

import cz.uhk.pproproject.model.TaskTime;
import cz.uhk.pproproject.model.TimePerUser;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.repository.TaskTimeRepository;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TimeController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskTimeRepository taskTimeRepository;

    @GetMapping("/dashboard/timeOverview")
    public String showTimeOverview(Model m) {
        List<User> userRepo = userRepository.findAllActive();

        List<TimePerUser> timeOverview = new ArrayList<>();
        Duration durationPerUser = Duration.ofDays(0);

        for (User user : userRepo) {
            List<TaskTime> timeRepo = taskTimeRepository.findTimeByUser(user);
            for (TaskTime taskTime : timeRepo) {
                durationPerUser = Duration.between(taskTime.getStartTime(), taskTime.getEndTime());
            }
            timeOverview.add(new TimePerUser(user.getFullName(), durationPerUser.toHours() + ((float) durationPerUser.toMinutesPart() / 60)));
            durationPerUser = Duration.ofDays(0);
        }

        m.addAttribute("timeOverview", timeOverview);
        return "timetracked/timeOverview";
    }

}
