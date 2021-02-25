package com.gsc.bm.api.ws;

import com.gsc.bm.model.game.IllegalMoveException;
import com.gsc.bm.model.game.Move;
import com.gsc.bm.service.session.GameSessionService;
import com.gsc.bm.service.view.ViewExtractorService;
import com.gsc.bm.service.view.model.client.ClientGameView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameSessionService gameSessionService;

    private final ViewExtractorService viewExtractorService;

    @Autowired
    public GameController(SimpMessagingTemplate messagingTemplate,
                          GameSessionService gameSessionService,
                          ViewExtractorService viewExtractorService) {
        this.messagingTemplate = messagingTemplate;
        this.gameSessionService = gameSessionService;
        this.viewExtractorService = viewExtractorService;
    }


    @MessageMapping("/game/{gameId}/move")
    @SendTo("/topic/game/{gameId}/move")
    public String makeYourMove(@DestinationVariable String gameId, @Payload Move move) throws IllegalMoveException {
        gameSessionService.submitMoveToGame(move, () -> messagingTemplate.convertAndSend("/topic/game/" + gameId + "/update", move.getGameId()));
        return move.getPlayerId() + " submitted a Move";
    }

    @MessageMapping("/game/{gameId}/{playerId}/view")
    @SendToUser("/queue/game/{gameId}/{playerId}/view")
    public ClientGameView getGameView(@DestinationVariable String gameId, @DestinationVariable String playerId) {
        return viewExtractorService.extractViewFor(gameSessionService.getGame(gameId), playerId);
    }

    @MessageMapping("/game/{gameId}/{playerId}/leave")
    public void leaveGame(@DestinationVariable String gameId, @DestinationVariable String playerId) {
        gameSessionService.leaveGame(gameId, playerId);
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/player/action/illegalMove")
    public String handleException(IllegalMoveException exception) {
        return exception.getWhatHeDid();
    }

}
