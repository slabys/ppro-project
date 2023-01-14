package cz.uhk.pproproject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class Stats {
    @Getter @Setter
    private int numOfEmployees;

    @Getter @Setter
    private int numOfManagers;

    @Getter @Setter
    private int numOfUsers;

    @Getter @Setter
    private float averageSalary;

    @Getter @Setter
    private float sumOfSalary;

    @Getter @Setter
    private int numOfProjects;

    @Getter @Setter
    private int numOfProjectsWithoutOwner;

    @Getter @Setter
    private long numOfTasks;

    @Getter @Setter
    private long numOfNonFinishedTasks;

    @Getter @Setter
    private long numOfFinishedTasks;

    @Getter @Setter
    private float averageUsersOnProject;
}
