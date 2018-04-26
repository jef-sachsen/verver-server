package de.ul.swtp.security.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class JwtAuthenticationResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;

    @Getter
    private final String access_token;

    public JwtAuthenticationResponse(String token) {
        this.access_token = token;
    }
}
