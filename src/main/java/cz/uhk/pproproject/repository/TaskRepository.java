package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Task;
import cz.uhk.pproproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    @Query("SELECT u FROM Task u WHERE u.assignedToProject = ?1")
    public List<Task> findAllTaskByAssignedProjectId(Long assignedProjectId);
}
