package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Contact;
import cz.uhk.pproproject.model.Project;
import cz.uhk.pproproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Long> {

}
