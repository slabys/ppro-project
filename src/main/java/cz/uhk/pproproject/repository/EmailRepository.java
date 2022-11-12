package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Email;
import cz.uhk.pproproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmailRepository extends JpaRepository<Email,Long> {
}
