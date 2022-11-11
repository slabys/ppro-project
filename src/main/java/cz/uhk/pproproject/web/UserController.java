package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.RoleEnum;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.model.UserActivationToken;
import cz.uhk.pproproject.repository.UserActivationTokenRepository;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserActivationTokenRepository uatRepo;

    // User activation
    @GetMapping("/activateUser/{token}")
    public String activateUser(@PathVariable String token){
        //TODO: check if UserActivationToken exists, check if user is not active, user registration form - set password all additional info -> post to /activateUser
        return "activateUserForm";
    }

    @PostMapping("/activateUser")
    public void activateUserPost(Model m, @ModelAttribute User userToActivate, HttpServletResponse resp) throws IOException {

        //TODO: add validation of data, update user in db (activate), add hashed password
        resp.sendRedirect("/mainPage");
    }

    @GetMapping("/registerUser")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        return "signup";
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @Transactional
    @PostMapping("/registerUser")
    public void processRegister(Model m, User user, Environment env, HttpServletResponse resp) throws IOException {
        //TODO set this for our auth
        //BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //String encodedPassword = passwordEncoder.encode(user.getPassword());
        //user.setPassword(encodedPassword);


        User userSearch = userRepo.findByEmail(user.getEmail());
        if(userSearch == null){
            user.setRole(RoleEnum.EMPLOYEE);
            user.setActive(false);
            userRepo.save(user);
        }else{
            if(userSearch.isActive()){
                m.addAttribute("errors", "User is already active!");
                resp.sendRedirect("registerUser");
            }
            UserActivationToken activeToken = uatRepo.findActiveByUser(user);
            Date today = new Date();
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, Integer.parseInt(env.getProperty("userAccounts.daysForExpire")));  // number of days to add
            if(activeToken == null){
                UserActivationToken uat = new UserActivationToken(user, UUID.randomUUID().toString(),c.getTime());
                uatRepo.save(uat);
                //TODO SEND EMAIL
                resp.sendRedirect("/registerSuccessfull");
            }else{
                if(activeToken.getExpireDate().after(new Date())){
                    activeToken.setExpireDate(c.getTime());
                    uatRepo.save(activeToken);
                    m.addAttribute("errors", "Token expire date is updated!");
                    resp.sendRedirect("/registerSuccessfull");
                }else{
                    m.addAttribute("errors", "Token exists and is not expired!");
                    resp.sendRedirect("registerUser");
                }
            }
        }
    }


    //test of permissions + retrieving logged user data
    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String listUsers(Model model, Authentication auth) {
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("listUsers", listUsers);

        /* GET USER IN CONTROLLER */
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        System.out.println(userDetails.getAuthorities());
        //System.out.println(userDetails.getUser().getRole());
        //System.out.println(userDetails.getAuthorities());

        return "users";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}
