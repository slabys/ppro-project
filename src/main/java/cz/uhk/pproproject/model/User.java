package cz.uhk.pproproject.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(nullable = false, unique = true, length = 45)
    private String email;
    @Column
    private String username;
    @Column(nullable = false, length = 20)
    private String firstName;
    @Column(nullable = false, length = 20)
    private String lastName;
    @Column
    private String password;
    @Column
    private float salary;
    @Column
    private CompanyPositionEnum role;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column
    private boolean active;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public CompanyPositionEnum getRole() {
        return role;
    }

    public void setRole(CompanyPositionEnum role) {
        this.role = role;
    }

/*
    public Set<Project> getAssignedProjects() {
        return assignedProjects;
    }

    public void setAssignedProjects(Set<Project> assignedProjects) {
        this.assignedProjects = assignedProjects;
    }

    @Column
    @ManyToMany()
    private Set<Project> assignedProjects;
*/
}

