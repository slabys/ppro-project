package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.Setter;

    import java.util.Map;

public class TimePerUser {
    @Getter @Setter
    String userName;
    @Getter @Setter
    float totalDuration;
    @Getter @Setter
    Map<String, Float> timeByTask;

    public TimePerUser(String userName, float totalDuration, Map<String, Float> timeByTask) {
        this.userName = userName;
        this.totalDuration = totalDuration;
        this.timeByTask = timeByTask;
    }
}
