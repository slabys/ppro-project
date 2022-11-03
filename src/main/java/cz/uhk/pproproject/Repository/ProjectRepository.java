package cz.uhk.pproproject.Repository;

import cz.uhk.pproproject.model.Project;
import cz.uhk.pproproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,Long> {

}
