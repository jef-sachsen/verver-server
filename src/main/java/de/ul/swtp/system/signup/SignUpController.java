package de.ul.swtp.system.signup;

import de.ul.swtp.system.User;
import de.ul.swtp.system.UserManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/signup")
public class SignUpController {

    @Value("${mv.signup.expiration}")
    private Long expiration;

    @Value("${mv.url.client}")
    private String urlClient;

    @Value("${mv.url.login}")
    private String urlLogin;

    private ApplicationEventPublisher eventPublisher;

    private final UserManagerImpl userManager;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    public SignUpController(UserManagerImpl userManager, EmailVerificationTokenRepository emailVerificationTokenRepository, ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.userManager = userManager;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<User> signUp(@RequestBody User user) {
        // Create user, set to disabled
        user.setId(null);
        user.setEnabled(false);
        user.setAdmin(false);
        user = userManager.signUpUser(user);

        // Create Token and persist it
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setUser(user);
        emailVerificationToken.generateToken();
        emailVerificationToken.setIssueDate(new Timestamp(System.currentTimeMillis()));
        emailVerificationTokenRepository.save(emailVerificationToken);

        // Send email to the user via system event
        eventPublisher.publishEvent(new SendEmailOnSignUpCompleteEvent(this, emailVerificationToken));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(path = "/{emailVerificationTokenString}")
    @ResponseBody
    public ResponseEntity<User> verifySignUp(@PathVariable String emailVerificationTokenString) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.getEmailVerificationTokenByToken(emailVerificationTokenString);

        Long currentTime = System.currentTimeMillis();
        Long tokenIssueTime = emailVerificationToken.getIssueDate().getTime();

        if (currentTime - tokenIssueTime < expiration) {
            userManager.enableUser(emailVerificationToken.getUser().getId());

            //Prepare response to redirect
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", urlClient + urlLogin);
            return new ResponseEntity<>(headers, HttpStatus.PERMANENT_REDIRECT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
