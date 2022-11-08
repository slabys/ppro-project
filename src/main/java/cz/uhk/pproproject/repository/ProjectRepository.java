package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {

}
