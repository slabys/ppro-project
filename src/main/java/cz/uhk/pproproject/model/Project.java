package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="project")
@NoArgsConstructor
public class Project extends BaseModel{
    @Column @Getter @Setter
    private String name;

    @Column @Getter @Setter
    private String url;

    @Column @Getter @Setter
    private String additionalInfo;

    @Column @Getter @Setter
    private String description;

    @Getter @Setter @OneToOne(targetEntity = User.class)
    private User projectOwner;

    @ManyToMany(mappedBy = "projects") @Getter @Setter
    private Set<User> usersOnProject = new HashSet<User>();

}
