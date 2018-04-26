package de.ul.swtp.modules.messaging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class SimpleMailMessageWrapper extends SimpleMailMessage {

    @Value("${mv.email.from}")
    private String from;

    @Value("${mv.email.subject-prefix}")
    private String prefix;

    public SimpleMailMessageWrapper() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);

    }

    @Override
    public void setSubject(String subject) {
        String prefixedSubject = prefix + subject;
        super.setSubject(prefixedSubject);
    }
}
