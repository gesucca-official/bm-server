package com.gsc.bm.service.session;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface AuthService {

    boolean isUsernameAvailable(String username);

    boolean isMailAvailable(String email);

    void sendVerificationEmail(String username, String address);

    void completeRegistration(String username, String password, String email, String code);

    UsernamePasswordAuthenticationToken getAuthTokenOrFail(String username, String password);

}
