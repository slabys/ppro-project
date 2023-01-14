package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="project")
@NoArgsConstructor
public class Project extends BaseModel{
    @Column @Getter @Setter
    private String name;

    @Column(nullable = true) @Getter @Setter
    private String url;

    @Column(nullable = true) @Getter @Setter
    private String projectInfo;

    @Column(nullable = false) @Getter @Setter
    private String description;

    @Getter @Setter @OneToOne(targetEntity = User.class)
    private User projectOwner;

    @ManyToMany(mappedBy = "projects") @Getter @Setter
    private Set<User> usersOnProject = new HashSet<User>();

    @Getter @Setter
    @OneToMany(targetEntity = Comment.class)
    @JoinTable(name = "project_comments",
            joinColumns = { @JoinColumn(name = "project_id") },
            inverseJoinColumns = { @JoinColumn(name = "project_comment_id") })
    private List<Comment> projectComments = new java.util.ArrayList<>();

    public void addComment(Comment comment){
        projectComments.add(comment);
    }

    public boolean canUserEditProject(User user){
        if(this.projectOwner!=null){
            return (this.projectOwner.getId().equals(user.getId()) || user.getRole() == RoleEnum.ADMIN || user.getRole() == RoleEnum.OWNER);
        }else{
            return (user.getRole() == RoleEnum.ADMIN || user.getRole() == RoleEnum.OWNER);
        }
    }
}
