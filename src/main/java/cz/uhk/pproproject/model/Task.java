package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
public class Task extends BaseModel{
    @Getter @Setter @OneToOne(targetEntity = User.class)
    private User createdBy;

    @Column @Getter @Setter
    private String name;

    @Getter @Setter @Column
    private String content;

    @Getter @Setter
    @OneToMany(targetEntity = Comment.class)
    @JoinTable(name = "task_comments",
            joinColumns = { @JoinColumn(name = "task_id") },
            inverseJoinColumns = { @JoinColumn(name = "task_comment_id") })
    private List<Comment> taskComments = new java.util.ArrayList<>();

    public void addComment(Comment comment){
        taskComments.add(comment);
    }
}
