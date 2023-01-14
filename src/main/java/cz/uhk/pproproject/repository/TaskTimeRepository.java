package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Task;
import cz.uhk.pproproject.model.TaskTime;
import cz.uhk.pproproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskTimeRepository extends JpaRepository<TaskTime,Long> {
    @Query("select u from TaskTime u where u.user = ?1 and u.task = ?2")
    public List<TaskTime> findAllUserTimes(User loggedUser, Optional<Task> task);

    @Query("select u from TaskTime u where u.user = ?1")
    public List<TaskTime> findTimeByUser(User loggedUser);
}
