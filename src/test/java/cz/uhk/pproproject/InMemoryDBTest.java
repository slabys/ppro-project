package cz.uhk.pproproject;

import cz.uhk.pproproject.model.RoleEnum;
import cz.uhk.pproproject.model.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = { Config.class },
        loader = AnnotationConfigContextLoader.class)
@Transactional
public class InMemoryDBTest {

    @Resource
    private UserTestRepo userTestRepo;

    @Test
    public void checkUser() {
        User newUser = new User("user@email.com", "User", "Luser", RoleEnum.ADMIN);
        userTestRepo.save(newUser);
        User user = userTestRepo.findByEmail("user@email.com");
        assertEquals("", user.getEmail());
    }
}
