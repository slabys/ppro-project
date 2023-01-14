package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Comment;
import cz.uhk.pproproject.model.Project;
import cz.uhk.pproproject.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
