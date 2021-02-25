package com.gsc.bm.service.session;

import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;

public interface ConnectionsService {

    void broadcastUsersStatus();

    Set<String> getAllConnectedUsers();

    void userConnected(SessionConnectEvent event);

    String userDisconnected(SessionDisconnectEvent event);

    void userActivityChanged(String userId, String activity);

}
