package de.ul.swtp.modules.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class JavaMailSenderConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("regulus.uberspace.de");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("mv@felixfoertsch.de");
        javaMailSender.setPassword("zWiXUjNwWxsNNG2mW2vP5uQUzap85Q");

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return javaMailSender;
    }
}
