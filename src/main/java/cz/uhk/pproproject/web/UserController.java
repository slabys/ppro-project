package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.Email;
import cz.uhk.pproproject.model.RoleEnum;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.model.UserActivationToken;
import cz.uhk.pproproject.repository.EmailRepository;
import cz.uhk.pproproject.repository.UserActivationTokenRepository;
import cz.uhk.pproproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@PropertySource("classpath:application.properties")
@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserActivationTokenRepository uatRepo;
    @Autowired
    private EmailRepository emailRepo;

    @Value("${userAccounts.daysForExpire}")
    private int expirationDays;

    // User activation
    @GetMapping("/activateUser/{token}")
    public String activateUser(Model m, @PathVariable String token, RedirectAttributes redirectAttrs) throws IOException {
        UserActivationToken uat = uatRepo.findByToken(token);
        if(uat != null){
            if(uat.getExpireDate().before(new Date())){
                redirectAttrs.addFlashAttribute("error", "Activation token doesn't exist!");
                return "redirect: /activateAccount";
            }
            Optional<User> user = userRepo.findById(uat.getUser().getId());
            if(user.isPresent()){
                m.addAttribute("user",user);
                m.addAttribute("uat",uat);
                return "activateAccount";
            }
        }else{
            redirectAttrs.addFlashAttribute("error", "Activation token doesn't exist!");
            return "redirect:/" ;
        }
        //TODO: check if UserActivationToken exists, check if user is not active, user registration form - set password all additional info -> post to /activateUser
        return null;
    }

    @PostMapping("/activateUser")
    @Transactional
    public String activateUserPost(Model m, User user,RedirectAttributes redirectAttrs) throws IOException {

        //TODO: add validation of data
        User updateUser = (User) userRepo.findByEmail(user.getEmail());
        updateUser.setActive(true);
        UserActivationToken uat = uatRepo.findActiveByUser(updateUser);
        uat.setTokenUsed(true);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        updateUser.setPassword(encodedPassword);
        userRepo.save(updateUser);
        uatRepo.save(uat);

        //TODO email edit link
        Email successEmail = new Email("New account on Employerr platform was successfully activated!", "no-reply@employerr.com", "Your account was successfully activated. Please revise your data. If there is something incorrect contact owner of company to update your data.", updateUser);
        emailRepo.save(successEmail);
        //TODO: send email about successful activation

        redirectAttrs.addFlashAttribute("info","Activation was successful");
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/registerUser")
    public String showRegistrationForm(Model model, Authentication auth) {
        model.addAttribute("user", new User());

        RoleHierarchyImpl roleHierarchy = RoleEnum.getRoleHierarchy();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

            Collection<? extends GrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority((userDetails.getUser().getRoleWithPrefix())));
            Collection<GrantedAuthority> ga = roleHierarchy.getReachableGrantedAuthorities(roles);
            Collection<RoleEnum> reachableRoles = new ArrayList<RoleEnum>();
            for (GrantedAuthority role: ga) {
                reachableRoles.add(RoleEnum.getRoleWithoutPrefix(role.toString()));
            }
            model.addAttribute("reachableRoles", reachableRoles);

        return "signupEmployee";
    }

    @Transactional
    @PostMapping("/registerUser")
    public String processRegister(Model m, User user, RedirectAttributes redirectAttrs) throws IOException {
        System.out.println(user.getRole());
        User userSearch = userRepo.findByEmail(user.getEmail());
        if(userSearch != null && userSearch.isActive()){
            redirectAttrs.addFlashAttribute("error", "User is already active!");
            return "redirect:/registerUser";
        }else {
            UserActivationToken activeToken = uatRepo.findActiveByUserEmail(user.getEmail());
            Date today = new Date();
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, expirationDays);  // number of days to add
            if (activeToken == null) {
                user.setActive(false);
                userRepo.save(user);
                UserActivationToken uat = new UserActivationToken(user, UUID.randomUUID().toString(), c.getTime());
                uatRepo.save(uat);

                //TODO email edit link
                Email tokenEmail = new Email("New account activation on Employerr platform", "no-reply@employerr.com", "Activate your account at this link: http://localhost:8080/activateUser/" + uat.getToken(), user);
                emailRepo.save(tokenEmail);
                //TODO implement mail sending on deployment env.

                redirectAttrs.addFlashAttribute("info","Account created successfully");
                return "redirect:/";
            } else {
                if (activeToken.getExpireDate().before(new Date())) {
                    activeToken.setExpireDate(c.getTime());
                    uatRepo.save(activeToken);
                    redirectAttrs.addFlashAttribute("info","Token expiration date extended to 30 days.");
                    return "redirect:/";
                } else {
                    redirectAttrs.addFlashAttribute("error", "Token exists and is not expired!");
                    return "redirect:/registerUser";
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
    public String login(Authentication auth,RedirectAttributes redirectAttrs){
        if(auth != null){
            redirectAttrs.addFlashAttribute("error", "User is already logged in!");
            return "redirect:/";
        }else{
        return "login";
        }
    }
}
