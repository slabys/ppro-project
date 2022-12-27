package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {

}
