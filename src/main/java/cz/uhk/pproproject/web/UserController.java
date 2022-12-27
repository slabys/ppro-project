package cz.uhk.pproproject.web;

import cz.uhk.pproproject.middleware.CustomUserDetails;
import cz.uhk.pproproject.model.*;
import cz.uhk.pproproject.repository.ContactRepository;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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
    private ContactRepository contactRepository;
    @Autowired
    private EmailRepository emailRepo;

    @Value("")
    private String repeatPassword;
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
        if (uat == null) {
            redirectAttrs.addFlashAttribute("error", "Activation token doesn't exist!");
            return "redirect:/";
        }
        if (uat.getExpireDate().before(new Date())) {
            redirectAttrs.addFlashAttribute("error", "Activation token has expired!");
            return "redirect:/";
        }
        Optional<User> user = userRepo.findById(uat.getUser().getId());
        if (user.isPresent()) {
            m.addAttribute("user", user.get());
            m.addAttribute("repeatPassword", this.repeatPassword);
            m.addAttribute("uat", uat);
            return "forms/user/activateAccount";
        }
        return "redirect:/";
    }

    @PostMapping("/activateUser")
    @Transactional
    public String activateUserPost(Model m, User user, RedirectAttributes redirectAttrs) throws IOException {
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
            emailService.sendSimpleMail(new EmailDetails(successEmail.getSendTo().getRegistrationEmail(), successEmail.getContent(), successEmail.getSubject(), null));

        redirectAttrs.addFlashAttribute("info", "You have successfully activated your account. You may now login into system.");
        return "redirect:/login";
    }

    @PreAuthorize("hasRole('ROLE_OWNER')")
    @GetMapping("/dashboard/registerUser")
    public String showRegistrationForm(Model model, Authentication auth) {
        model.addAttribute("user", new User());

        RoleHierarchyImpl roleHierarchy = RoleEnum.getRoleHierarchy();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        Collection<? extends GrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority((userDetails.getUser().getRoleWithPrefix())));
        Collection<GrantedAuthority> ga = roleHierarchy.getReachableGrantedAuthorities(roles);
        ArrayList<RoleEnum> reachableRoles = new ArrayList<RoleEnum>();
        for (GrantedAuthority role : ga) {
            reachableRoles.add(RoleEnum.getRoleWithoutPrefix(role.toString()));
        }
        Collections.sort(reachableRoles);
        model.addAttribute("reachableRoles", reachableRoles);

        return "forms/user/registerUser";
    }

    @Transactional
    @PostMapping("/dashboard/registerUser")
    public String processRegister(Model m, User user, RedirectAttributes redirectAttrs) throws IOException {
        User userSearch = userRepo.findByEmail(user.appendCompanyEmail(user.getEmail()));
        UserActivationToken activeToken = uatRepo.findActiveByUserEmail(user.appendCompanyEmail(user.getEmail()));
        Contact contact = new Contact();
        contactRepository.save(contact);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, expirationDays);

        if (userSearch != null) {
            if (user.isActive())
                redirectAttrs.addFlashAttribute("error", "User already exists! User has been confirmed!");
            if (!user.isActive())
                redirectAttrs.addFlashAttribute("error", "User already exists! User has not been confirmed");
            return "redirect:/dashboard/registerUser";
        }

        if (activeToken == null) {
            user.setActive(false);
            user.setEmail(user.appendCompanyEmail(user.getEmail()));
            user.setRegistrationEmail(user.getRegistrationEmail());
            user.setFirstName(user.getFirstName().substring(0, 1).toUpperCase() + user.getFirstName().substring(1));
            user.setLastName(user.getLastName().substring(0, 1).toUpperCase() + user.getLastName().substring(1));
            user.setContact(contact);
            userRepo.save(user);

            UserActivationToken userActivationToken = new UserActivationToken(user, UUID.randomUUID().toString(), calendar.getTime());
            uatRepo.save(userActivationToken);

            // Send e-mail with registration link to set password
            Email tokenEmail = new Email("New account activation on Employerr platform", "no-reply@employerr.com", "Activate your account at this link: " + domain + "activateUser/" + userActivationToken.getToken(), user);
            emailRepo.save(tokenEmail);
            if (Objects.equals(env, "prod"))
                emailService.sendSimpleMail(new EmailDetails(tokenEmail.getSendTo().getRegistrationEmail(), tokenEmail.getContent(), tokenEmail.getSubject(), null));

            redirectAttrs.addFlashAttribute("info", "Account created successfully");
            return "redirect:/";
        }
        return "redirect:/";
    }

    @GetMapping("/dashboard/users")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String listUsers(Model model, Authentication auth) {
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("listUsers", listUsers);

        return "user/userList";
    }

    @GetMapping("/login")
    public String login(Authentication auth, RedirectAttributes redirectAttrs) {
        if (auth != null) {
            redirectAttrs.addFlashAttribute("error", "User is already logged in!");
            return "redirect:/";
        }
        return "forms/user/login";
    }

    @GetMapping("/dashboard/user/detail/{id}")
    public String showUserDetails(Model m, @PathVariable long id, RedirectAttributes redirectAttrs) {
        Optional<User> user = userRepo.findById(id);
        if (user.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "This user does not exists!");
            throw new ResponseStatusException(NOT_FOUND, "Unable to find user details");
        }
        m.addAttribute("user", user.get());
        return "user/userDetail";
    }

    @GetMapping("/dashboard/user/editMyAccount")
    public String showUserEditForm(Model m, Authentication auth) {
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Optional<User> user = userRepo.findById(userDetails.getUser().getId());

        user.ifPresent(value -> {
            m.addAttribute("user", value);
            if (user.get().getContact() == null) {
                m.addAttribute("contact", new Contact());
            } else {
                m.addAttribute("contact", user.get().getContact());
            }
        });

        return "forms/user/editAccount";
    }

    @PostMapping("/dashboard/user/edit")
    public String editLoggedUser(RedirectAttributes redirectAttributes, User user, Contact contact) {
        User findUser = userRepo.findByEmail(user.getEmail());

        contact.setEmptyValuesToNull();
        contactRepository.save(contact);
        findUser.setContact(contact);

        userRepo.save(findUser);
        redirectAttributes.addFlashAttribute("info", "User edited successfully");
        return "redirect:/";
    }
}