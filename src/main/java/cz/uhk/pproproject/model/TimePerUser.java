package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.Setter;

public class TimePerUser {
    @Getter @Setter
    String userName;
    @Getter @Setter
    float duration;

    public TimePerUser(String userName, float duration) {
        this.userName = userName;
        this.duration = duration;
    }
}
