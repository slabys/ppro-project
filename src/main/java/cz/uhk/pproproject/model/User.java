package cz.uhk.pproproject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
public class User extends BaseModel implements Comparable<User>{
    public User(String email, String firstName, String lastName, RoleEnum role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    @Getter @Setter
    @OneToOne(targetEntity = Contact.class, fetch = FetchType.EAGER)
    private Contact contact;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    private RoleEnum role;
    @Column(nullable = false, unique = true, length = 45) @Setter @Getter
    private String email;
    @Column(nullable = false, length = 20) @Setter @Getter
    private String firstName;
    @Column(nullable = false, length = 20) @Setter @Getter
    private String lastName;

    @Column @Setter @Getter
    private String password;

    @Column @Setter @Getter
    private float salary;

    @Column @Setter @Getter
    private boolean active;

    public String getRoleWithPrefix(){
        return RoleEnum.getRoleWithPrefix(this.role);
    }
    public String appendCompanyEmail(String email){
        return email + "@employerr.com";
    }
    public void setRole(String role){
        if(role.contains("ROLE_")) role.replace("ROLE_","");
        this.role = RoleEnum.valueOf(role);
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }


    @Getter
    @Setter
    @ManyToMany(targetEntity = Project.class, cascade = { CascadeType.ALL },fetch = FetchType.EAGER)
    @JoinTable(name = "user_project",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "project_id") })

    private List<Project> projects;
    public boolean hasAccessToProject(Project project){
        return projects.contains(project);
    }
    public void addProject(Project project){
        this.projects.add(project);
    }
    public void removeFromProject(Project project) {this.projects.remove(project);}
    public boolean hasHigherRole(){
        return (this.role == RoleEnum.ADMIN || this.role == RoleEnum.OWNER);
    }
    public String getFullName(){
        return firstName + " " + lastName;
    }

    @Override
    public int compareTo(User user){
        int last = this.firstName.compareTo(user.firstName);
        //Sorting by first name if last name is same d
        return last == 0 ? this.lastName.compareTo(user.lastName) : last;
    }
}

