package com.gsc.bm.api.ws;

import com.gsc.bm.service.factories.GameFactoryService;
import com.gsc.bm.service.session.GameSessionService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class OpenGameQueuesController {

    private final GameFactoryService gameFactoryService;
    private final GameSessionService gameSessionService;

    public OpenGameQueuesController(GameFactoryService gameFactoryService,
                                    GameSessionService gameSessionService) {
        this.gameFactoryService = gameFactoryService;
        this.gameSessionService = gameSessionService;
    }

    @MessageMapping("/game/open/1vCom/join/{username}/{deckId}")
    @SendToUser("/game/open/1vCom/ready")
    public String queueForOpen1vCom(@DestinationVariable String username, @DestinationVariable String deckId) {
        return gameSessionService.newGame(
                gameFactoryService.craftOpen1vComGame(username, deckId));
    }

}
