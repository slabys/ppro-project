package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    @Query("select count(u) from Project u ")
    public int countAll();

    @Query("select count(p) from Project p where p.projectOwner is null")
    int countWithoutOwner();

}
