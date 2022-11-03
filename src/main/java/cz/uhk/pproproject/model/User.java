package cz.uhk.pproproject.model;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
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
    @Column
    private String username;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String password;
    @Column
    private float salary;
    @Column
    private CompanyPosition role;

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

    public CompanyPosition getRole() {
        return role;
    }

    public void setRole(CompanyPosition role) {
        this.role = role;
    }

    public ArrayList<Project> getAssignedProjects() {
        return assignedProjects;
    }

    public void setAssignedProjects(ArrayList<Project> assignedProjects) {
        this.assignedProjects = assignedProjects;
    }

    @Column
    @ManyToMany()
    private ArrayList<Project> assignedProjects;
}
