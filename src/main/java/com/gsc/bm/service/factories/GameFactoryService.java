package com.gsc.bm.service.factories;

import com.gsc.bm.model.game.Game;
import com.gsc.bm.service.session.model.QueuedPlayer;

import java.util.List;

public interface GameFactoryService {

    Game craftQuick1vComGame(String playerId);

    Game craftOpen1vComGame(String username, String deckId);

    Game craftQuickMultiPlayerGame(List<QueuedPlayer> players);

}
