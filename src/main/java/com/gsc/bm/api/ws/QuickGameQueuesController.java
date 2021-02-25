package com.gsc.bm.api.ws;

import com.gsc.bm.service.factories.GameFactoryService;
import com.gsc.bm.service.session.GameSessionService;
import com.gsc.bm.service.session.QueueService;
import com.gsc.bm.service.session.model.QueuedPlayer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class QuickGameQueuesController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameFactoryService gameFactoryService;
    private final GameSessionService gameSessionService;
    private final QueueService queueService;

    public QuickGameQueuesController(SimpMessagingTemplate messagingTemplate,
                                     GameFactoryService gameFactoryService,
                                     GameSessionService gameSessionService,
                                     QueueService queueService) {
        this.messagingTemplate = messagingTemplate;
        this.gameFactoryService = gameFactoryService;
        this.gameSessionService = gameSessionService;
        this.queueService = queueService;
    }


    @MessageMapping("/game/quick/1vCom/join")
    @SendToUser("/queue/game/quick/1vCom/ready")
    public String queueForQuick1vCom(String playerId) {
        return gameSessionService.newGame(
                gameFactoryService.craftQuick1vComGame(playerId));
    }

    @MessageMapping("/game/quick/1v1/join")
    @SendTo("/topic/game/quick/1v1/ready")
    public synchronized String queueForQuick1v1(String playerId) {
        return queueFor(QueueService.GameQueue.Q_1V1, playerId, true);
    }

    @MessageMapping("/game/quick/ffa/join")
    @SendTo("/topic/game/quick/ffa/joined")
    public synchronized List<QueuedPlayer> queueForQuickFfa(String playerId) {
        String game = queueFor(QueueService.GameQueue.Q_FFA, playerId, true);
        if (game != null)
            messagingTemplate.convertAndSend("/topic/game/quick/ffa/ready", game);
        return queueService.getUsersInQueue(QueueService.GameQueue.Q_FFA);
    }

    @MessageMapping("/game/quick/ffa/join/com")
    @SendTo("/topic/game/quick/ffa/joined")
    public synchronized List<QueuedPlayer> addComPlayerToFfaQueue() {
        String game = queueFor(QueueService.GameQueue.Q_FFA, "QueuedComPlayer", false);
        if (game != null)
            messagingTemplate.convertAndSend("/topic/game/quick/ffa/ready", game);
        return queueService.getUsersInQueue(QueueService.GameQueue.Q_FFA);
    }

    @MessageMapping("/game/quick/ffa/start")
    @SendTo("/topic/game/quick/ffa/ready")
    public synchronized String forceStartFfaGame() {
        Optional<List<QueuedPlayer>> queuedPlayers = queueService.flushQueue(QueueService.GameQueue.Q_FFA);
        if (queuedPlayers.isPresent()) {
            messagingTemplate.convertAndSend("/topic/game/quick/ffa/joined", queueService.getUsersInQueue(QueueService.GameQueue.Q_FFA));
            return gameSessionService.newGame(
                    gameFactoryService.craftQuickMultiPlayerGame(queuedPlayers.get()));
        }
        return null;
    }

    private String queueFor(QueueService.GameQueue queue, String playerId, boolean human) {
        Optional<List<QueuedPlayer>> queuedPlayers = queueService.joinQueue(
                new QueuedPlayer(playerId, human), queue);
        return queuedPlayers.map(players -> gameSessionService.newGame(
                gameFactoryService.craftQuickMultiPlayerGame(players))).orElse(null);
    }
}
