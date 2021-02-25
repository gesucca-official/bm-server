package com.gsc.bm.api.ws;

import com.gsc.bm.service.session.ConnectionsService;
import com.gsc.bm.service.session.QueueService;
import com.gsc.bm.service.session.model.QueuedPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class PresenceEventListener {

    private final ConnectionsService connectionsService;
    private final QueueService queueService;

    @Autowired
    public PresenceEventListener(ConnectionsService connectionsService, QueueService queueService) {
        this.connectionsService = connectionsService;
        this.queueService = queueService;
    }

    @EventListener
    public synchronized void handleSessionConnected(SessionConnectEvent event) {
        connectionsService.userConnected(event);
        connectionsService.broadcastUsersStatus();
    }

    @EventListener
    public synchronized void handleSessionDisconnect(SessionDisconnectEvent event) {
        String userId = connectionsService.userDisconnected(event);
        queueService.leaveAllQueues(new QueuedPlayer(userId, true));
        connectionsService.broadcastUsersStatus();
    }

    @MessageMapping("/connections/users/tell/me")
    public void broadcastUsers() {
        connectionsService.broadcastUsersStatus();
    }
}
