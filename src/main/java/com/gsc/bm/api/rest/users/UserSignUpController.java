package com.gsc.bm.api.rest.users;

import com.gsc.bm.service.session.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("rest/v1//sign-up")
public class UserSignUpController {

    private final AuthService authService;

    public UserSignUpController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping(value = "/users/{username}/available")
    public boolean isUsernameValid(@PathVariable("username") String username) {
        return authService.isUsernameAvailable(username);
    }

    @GetMapping(value = "/mail/{mail}/available")
    public boolean isEmailValid(@PathVariable("mail") String mail) {
        return authService.isMailAvailable(mail);
    }

    @PostMapping(value = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendVerificationMail(@RequestBody Map<String, String> credentials) {
        try {
            authService.sendVerificationEmail(credentials.get("username"), credentials.get("email"));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void completeRegistration(@RequestBody Map<String, String> credentials) {
        try {
            authService.completeRegistration(
                    credentials.get("username"),
                    credentials.get("password"),
                    credentials.get("email"),
                    credentials.get("code")
            );
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
