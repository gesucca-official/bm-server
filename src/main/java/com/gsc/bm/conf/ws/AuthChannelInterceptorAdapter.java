package com.gsc.bm.conf.ws;

import com.gsc.bm.service.session.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    private final AuthService authService;

    @Autowired
    public AuthChannelInterceptorAdapter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) throws AuthenticationException {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        assert accessor != null;
        if (StompCommand.CONNECT == accessor.getCommand()) {
            final String username = accessor.getLogin();
            final String password = accessor.getPasscode();
            try {
                final UsernamePasswordAuthenticationToken user = authService.getAuthTokenOrFail(username, password);
                accessor.setUser(user);
            } catch (AuthenticationException e) {
                return null;
            }
        }
        return message;
    }
}
