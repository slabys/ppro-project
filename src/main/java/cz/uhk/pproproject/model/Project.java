package cz.uhk.pproproject.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    private String name;

/*
    public Set<User> getUsersOnProject() {
        return usersOnProject;
    }

    public void setUsersOnProject(Set<User> usersOnProject) {
        this.usersOnProject = usersOnProject;
    }

    @ManyToMany(mappedBy = "assignedProjects")
    private Set<User> usersOnProject = new HashSet<User>();

 */

}
