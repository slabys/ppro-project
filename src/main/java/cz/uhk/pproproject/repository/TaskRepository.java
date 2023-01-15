package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Project;
import cz.uhk.pproproject.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    @Query("SELECT u FROM Task u WHERE u.assignedToProject = ?1 and u.completed=false order by u.createdAt desc")
    public List<Task> findAllTaskByAssignedProject(Project assignedProjectId);

    @Query("SELECT u FROM Task u WHERE u.assignedToProject = ?1 and u.completed=true order by u.createdAt desc")
    public List<Task> findAllFinishedTasksByAssignedProject(Project id);

    @Query("SELECT count(u) FROM Task u WHERE u.completed=false")
    public long countAllNoncompletedTasks();

    @Query("SELECT count(u) FROM Task u WHERE u.completed=true")
    public long countAllFinishedTasksByAssignedProject();
}
