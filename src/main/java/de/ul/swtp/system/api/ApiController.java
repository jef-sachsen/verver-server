package de.ul.swtp.system.api;

import de.ul.swtp.modules.messaging.SimpleMailMessageWrapper;
import de.ul.swtp.system.User;
import de.ul.swtp.system.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/api")
public class ApiController {

    private final UserManager userManager;
    private final JavaMailSender javaMailSender;

    @Autowired
    public ApiController(UserManager userManager, JavaMailSender javaMailSender) {
        this.userManager = userManager;
        this.javaMailSender = javaMailSender;
    }

    @PutMapping(path = "reset-password/{usernameOrEmail}")
    @ResponseBody
    public ResponseEntity<Page<User>> getUsers(@PathVariable String usernameOrEmail) {

        // find user by username
        // build jwt that allows "login" based on old password
        // redirect to reset-password page


        userManager.getUserByUsername(usernameOrEmail);
        SimpleMailMessageWrapper simpleMailMessageWrapper = new SimpleMailMessageWrapper();
        simpleMailMessageWrapper.setText("Please click on this link to reset your password:");
        javaMailSender.send(simpleMailMessageWrapper);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
