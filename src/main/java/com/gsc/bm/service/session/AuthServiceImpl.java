package com.gsc.bm.service.session;

import com.gsc.bm.repo.external.UserCredentialsRecord;
import com.gsc.bm.repo.external.UserCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;
    // TODO cache for this?
    private final UserCredentialsRepository userCredentialsRepo;

    private final Map<String, String> pendingVerificationCodes = new HashMap<>();

    @Autowired
    public AuthServiceImpl(JavaMailSender javaMailSender,
                           PasswordEncoder passwordEncoder,
                           UserCredentialsRepository userCredentialsRepo) {
        this.javaMailSender = javaMailSender;
        this.passwordEncoder = passwordEncoder;
        this.userCredentialsRepo = userCredentialsRepo;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return userCredentialsRepo.findById(username).isEmpty();
    }

    @Override
    public boolean isMailAvailable(String email) {
        return userCredentialsRepo.findAllByEmail(email).isEmpty();
    }

    @Override
    public void sendVerificationEmail(String username, String address) {
        if (!isUsernameAvailable(username) || !isMailAvailable(address))
            throw new BadCredentialsException("USERNAME or EMAIL already present in DB");
        pendingVerificationCodes.put(username, String.format("%06d", new Random().nextInt(999999)));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(address);
        message.setSubject("Welcome to BOTTE MICIDIALI, " + username + "!");
        message.setText("Hello " + username + ", thank you for joining us.\n" +
                "Here's your confirmation code: " + pendingVerificationCodes.get(username));
        javaMailSender.send(message);
    }

    @Override
    public void completeRegistration(String username, String password, String email, String code) {
        if (!code.equals(pendingVerificationCodes.get(username)))
            throw new BadCredentialsException("Invalid VERIFICATION CODE");
        if (!isUsernameAvailable(username) || !isMailAvailable(email))
            throw new BadCredentialsException("USERNAME or EMAIL already present in DB");
        userCredentialsRepo.save(new UserCredentialsRecord(username, email, passwordEncoder.encode(password), "USER"));
        pendingVerificationCodes.remove(username);
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthTokenOrFail(String username, String password) {
        if (username == null || username.trim().isEmpty())
            throw new AuthenticationCredentialsNotFoundException("USERNAME was null or empty");

        if (password == null || password.trim().isEmpty())
            throw new AuthenticationCredentialsNotFoundException("PASSWORD was null or empty");

        Optional<UserCredentialsRecord> rec = userCredentialsRepo.findById(username);
        if (rec.isEmpty())
            throw new BadCredentialsException("USERNAME " + username + " is not registered");
        else {
            if (!passwordEncoder.matches(password, rec.get().getSaltedHash()))
                throw new BadCredentialsException("Invalid PASSWORD for USER " + username);
            // if you are here, everything is fine
            return new UsernamePasswordAuthenticationToken(
                    username, null,
                    Collections.singleton((GrantedAuthority) () -> "USER"));
        }
    }
}
