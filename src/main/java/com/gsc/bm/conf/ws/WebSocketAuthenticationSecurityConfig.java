package com.gsc.bm.conf.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptorAdapter authInterceptor;

    @Autowired
    public WebSocketAuthenticationSecurityConfig(AuthChannelInterceptorAdapter authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void configureClientInboundChannel(final ChannelRegistration registration) {
        registration.interceptors(authInterceptor);
    }
}
