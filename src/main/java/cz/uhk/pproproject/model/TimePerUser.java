package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.Setter;

    import java.util.Map;

public class TimePerUser {
    @Getter @Setter
    User user;
    @Getter @Setter
    float totalDuration;
    @Getter @Setter
    Map<String, Float> timeByTask;

    public TimePerUser(User user, float totalDuration, Map<String, Float> timeByTask) {
        this.user = user;
        this.totalDuration = totalDuration;
        this.timeByTask = timeByTask;
    }
}
