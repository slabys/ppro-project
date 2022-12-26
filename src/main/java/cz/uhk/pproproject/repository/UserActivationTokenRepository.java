package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.model.UserActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserActivationTokenRepository extends JpaRepository<UserActivationToken,Long> {
    @Query("SELECT u FROM UserActivationToken u WHERE u.user = ?1 and u.tokenUsed = false")
    public UserActivationToken findActiveByUser(User user);

    @Query("SELECT ua FROM UserActivationToken ua join User u on u.id = ua.user.id WHERE u.email = ?1 and ua.tokenUsed = false")
    public UserActivationToken findActiveByUserEmail(String email);

    @Query("select u from UserActivationToken u where u.token = ?1 and u.tokenUsed = false")
    public UserActivationToken findByToken(String token);
}
