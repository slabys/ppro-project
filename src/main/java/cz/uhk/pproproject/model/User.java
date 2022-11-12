package cz.uhk.pproproject.model;

import javax.persistence.*;

@Entity

public class User extends BaseModel{
    public User(String email, String firstName, String lastName, RoleEnum role) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public User() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(nullable = false, unique = true, length = 45)
    private String email;
    @Column(nullable = false, length = 20)
    private String firstName;
    @Column(nullable = false, length = 20)
    private String lastName;
    @Column
    private String password;
    @Column
    private float salary;


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

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

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

