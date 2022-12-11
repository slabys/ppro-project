package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public User findByEmail(String email);

    @Query("SELECT u from User u where (u.role = 'ADMIN' or u.role = 'OWNER' or u.role = 'MANAGER')")
    public List<User> findAllHigherRoles();

}
