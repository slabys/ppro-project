package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.Email;
import cz.uhk.pproproject.model.RoleEnum;
import cz.uhk.pproproject.model.User;
import cz.uhk.pproproject.model.UserActivationToken;
import cz.uhk.pproproject.repository.EmailRepository;
import cz.uhk.pproproject.repository.UserActivationTokenRepository;
import cz.uhk.pproproject.repository.UserRepository;
import cz.uhk.pproproject.service.email.EmailDetails;
import cz.uhk.pproproject.service.email.EmailService;
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
    public EmailService emailService;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserActivationTokenRepository uatRepo;
    @Autowired
    private EmailRepository emailRepo;
    @Value("${userAccounts.daysForExpire}")
    private int expirationDays;

    @Value("${cz.uhk.pproproject.enviroment}")
    private String env;

    @Value("${cz.uhk.pproproject.domain}")
    private String domain;

    // User activation
    @GetMapping("/activateUser/{token}")
    public String activateUser(Model m, @PathVariable String token, RedirectAttributes redirectAttrs) throws IOException {
        UserActivationToken uat = uatRepo.findByToken(token);
        if (uat != null) {
            if (uat.getExpireDate().before(new Date())) {
                redirectAttrs.addFlashAttribute("error", "Activation token doesn't exist!");
                return "redirect: /activateAccount";
            }
            Optional<User> user = userRepo.findById(uat.getUser().getId());
            if (user.isPresent()) {
                m.addAttribute("user", user.get());
                m.addAttribute("uat", uat);
                return "forms/user/activateAccount";
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "Activation token doesn't exist!");
            return "redirect:/";
        }

        return null;
    }

    @PostMapping("/activateUser")
    @Transactional
    public String activateUserPost(Model m, User user, RedirectAttributes redirectAttrs) throws IOException {
        //TODO: user registration form - implement additional info -> post to /activateUser
        //TODO: add validation of data
        User updateUser = userRepo.findByEmail(user.getEmail());
        updateUser.setActive(true);
        UserActivationToken uat = uatRepo.findActiveByUser(updateUser);
        uat.setTokenUsed(true);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        updateUser.setPassword(encodedPassword);
        userRepo.save(updateUser);
        uatRepo.save(uat);

        Email successEmail = new Email("New account on Employerr platform was successfully activated!", "no-reply@employerr.com", "Your account was successfully activated. Please revise your data. If there is something incorrect contact owner of company to update your data.", updateUser);
        emailRepo.save(successEmail);
        if (Objects.equals(env, "prod"))
            emailService.sendSimpleMail(new EmailDetails(successEmail.getSendTo().getEmail(), successEmail.getContent(), successEmail.getSubject(), null));


        redirectAttrs.addFlashAttribute("info", "Activation was successful");
        return "redirect:/";
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/dashboard/registerUser")
    public String showRegistrationForm(Model model, Authentication auth) {
        model.addAttribute("user", new User());

        RoleHierarchyImpl roleHierarchy = RoleEnum.getRoleHierarchy();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        Collection<? extends GrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority((userDetails.getUser().getRoleWithPrefix())));
        Collection<GrantedAuthority> ga = roleHierarchy.getReachableGrantedAuthorities(roles);
        Collection<RoleEnum> reachableRoles = new ArrayList<RoleEnum>();
        for (GrantedAuthority role : ga) {
            reachableRoles.add(RoleEnum.getRoleWithoutPrefix(role.toString()));
        }
        model.addAttribute("reachableRoles", reachableRoles);

        return "forms/user/registerUser";
    }

    @Transactional
    @PostMapping("/dashboard/registerUser")
    public String processRegister(Model m, User user, RedirectAttributes redirectAttrs) throws IOException {
        User userSearch = userRepo.findByEmail(user.appendCompanyEmail(user.getEmail()));
        if (userSearch != null && user.isActive()) {
            redirectAttrs.addFlashAttribute("error", "User is already active!");
            return "redirect:/registerUser";
        } else {
            UserActivationToken activeToken = uatRepo.findActiveByUserEmail(user.appendCompanyEmail(user.getEmail()));

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, expirationDays);  // number of days to add to expiration days
            if (activeToken == null) {
                user.setActive(false);
                user.setEmail(user.appendCompanyEmail(user.getEmail()));
                user.setFirstName(user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1));
                user.setLastName(user.getLastName().substring(0, 1).toUpperCase() + user.getLastName().substring(1));
                userRepo.save(user);
                UserActivationToken uat = new UserActivationToken(user, UUID.randomUUID().toString(), c.getTime());
                uatRepo.save(uat);

                //Email notification
                Email tokenEmail = new Email("New account activation on Employerr platform", "no-reply@employerr.com", "Activate your account at this link: " + domain + "activateUser/" + uat.getToken(), user);
                emailRepo.save(tokenEmail);
                if (Objects.equals(env, "prod"))
                    emailService.sendSimpleMail(new EmailDetails(tokenEmail.getSendTo().getEmail(), tokenEmail.getContent(), tokenEmail.getSubject(), null));


                redirectAttrs.addFlashAttribute("info", "Account created successfully");
                return "redirect:/";
            } else {
                if (activeToken.getExpireDate().before(new Date())) {
                    activeToken.setExpireDate(c.getTime());
                    uatRepo.save(activeToken);
                    redirectAttrs.addFlashAttribute("info", "Token expiration date extended to 30 days.");
                    return "redirect:/";
                } else {
                    redirectAttrs.addFlashAttribute("error", "Token exists and is not expired!");
                    return "redirect:/registerUser";
                }
            }
        }
    }


    //test of permissions + retrieving logged user data
    @GetMapping("/dashboard/users")
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
    public String login(Authentication auth, RedirectAttributes redirectAttrs) {
        if (auth != null) {
            redirectAttrs.addFlashAttribute("error", "User is already logged in!");
            return "redirect:/";
        } else {
            return "forms/user/login";
        }
    }
}
