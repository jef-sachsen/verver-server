package de.ul.swtp.system.signup;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

class SendEmailOnSignUpCompleteEvent extends ApplicationEvent {

    @Getter
    private EmailVerificationToken emailVerificationToken;

    SendEmailOnSignUpCompleteEvent(Object source, EmailVerificationToken emailVerificationToken) {
        super(source);
        this.emailVerificationToken = emailVerificationToken;
    }
}
