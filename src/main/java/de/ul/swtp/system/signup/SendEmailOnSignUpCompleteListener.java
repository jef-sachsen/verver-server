package de.ul.swtp.system.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SendEmailOnSignUpCompleteListener implements ApplicationListener<SendEmailOnSignUpCompleteEvent> {

    private final JavaMailSender javaMailSender;

    @Value("${mv.email.from}")
    private String from;

    @Value("${mv.signup.email-subject}")
    private String subject;

    @Value("${mv.url.api}")
    private String urlServer;

    @Value("${mv.url.signup}")
    private String urlSignup;

    @Autowired
    public SendEmailOnSignUpCompleteListener(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /*
    FYI: The redirecting is not working yet because the link is pointing to an older version of
    our system which does not include the implementation if it. To test it just replace clientUrl with
    http://localhost:8080/.
     */
    @Override
    public void onApplicationEvent(SendEmailOnSignUpCompleteEvent event) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(event.getEmailVerificationToken().getUser().getUsername());
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(
                "Willkommen in Ihrer Mitgliederverwaltungssoftware!\n\n"
                        + "Um Ihr Konto zu aktivieren, klicken Sie bitte hier: " + urlServer + urlSignup + "/" + event.getEmailVerificationToken().getToken() + "\n"
                        + "Vielen Dank!\n\n"
                        + "Das mf17a-Team");
        javaMailSender.send(simpleMailMessage);
    }
}
