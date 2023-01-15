package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.Project;
import cz.uhk.pproproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public User findByEmail(String email);

    @Query("SELECT u from User u where (u.role = 'ADMIN' or u.role = 'OWNER' or u.role = 'MANAGER') and u.active = true")
    public List<User> findAllHigherRoles();

    @Query("SELECT u from User u where u.active = true")
    public List<User> findAllActive();

    @Query("SELECT distinct u from User u where u.active = true and u.role= 'EMPLOYEE' and not(:project member of u.projects) order by u.firstName, u.lastName")
    List<User> findAllEmployeesNotInProject(Project project);

    @Query("SELECT distinct  u from User u where u.active = true and u.role= 'MANAGER' and not(:project member of u.projects) order by u.firstName, u.lastName")
    List<User> findAllManagersNotInProject(Project project);

    @Query("SELECT distinct  u from User u where u.active = true and u.role = 'OWNER' and not(:project member of u.projects) order by u.firstName, u.lastName")
    List<User> findAllHighRolesNotInProject(Project project);

    @Query("SELECT distinct u from User u where u.active = true and u.role= 'EMPLOYEE' and (:project member of u.projects) order by u.firstName, u.lastName")
    List<User> findAllEmployeesInProject(Project project);

    @Query("SELECT distinct  u from User u where u.active = true and u.role= 'MANAGER' and (:project member of u.projects) order by u.firstName, u.lastName")
    List<User> findAllManagersInProject(Project project);

    @Query("SELECT distinct  u from User u where u.active = true and u.role = 'OWNER' and (:project member of u.projects) order by u.firstName, u.lastName")
    List<User> findAllHighRolesInProject(Project project);

    @Query("select count(u) from User u where u.active = true")
    public int countAllActive();

    @Query("select count(u) from User u where u.active = true and u.role='EMPLOYEE'")
    public int countAllEmployees();

    @Query("select count(u) from User u where u.active = true and u.role='MANAGER'")
    public int countAllManagers();

    @Query("select avg(u.salary) from User u where u.active = true")
    public float avgSalary();

    @Query("select sum(u.salary) from User u where u.active = true")
    public float sumSalary();
}
