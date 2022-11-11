package cz.uhk.pproproject.repository;

import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.model.UserActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserActivationTokenRepository extends JpaRepository<UserActivationToken,Long> {
    @Query("SELECT u FROM UserActivationToken u WHERE u.user = ?1 and u.tokenUsed = false")
    public UserActivationToken findActiveByUser(User user);
}
