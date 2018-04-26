package de.ul.swtp.system.signup;

import de.ul.swtp.system.User;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "mv_users_email_verification_token")
public class EmailVerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date issueDate;

    void generateToken() {
        this.token = UUID.randomUUID().toString();
    }
}
