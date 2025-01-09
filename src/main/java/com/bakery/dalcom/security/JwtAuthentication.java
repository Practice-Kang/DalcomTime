package com.bakery.dalcom.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;

public class JwtAuthentication extends UsernamePasswordAuthenticationToken {

    public JwtAuthentication(String username) {
        super(username, null, Collections.emptyList());
    }
}
