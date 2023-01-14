package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class TaskTime extends BaseModel{
    @Getter
    @Setter
    @OneToOne(targetEntity = User.class)
    private User user;

    @Column
    @Getter @Setter
    private LocalDateTime startTime;

    @Column
    @Getter @Setter
    private LocalDateTime endTime;

    @Getter @Setter @OneToOne(targetEntity = Task.class)
    private Task task;

    @Getter @Setter
    @OneToOne(targetEntity = Project.class)
    private Project project;
}
