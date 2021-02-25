package com.gsc.bm.service.factories;

import com.gsc.bm.model.game.Player;

public interface PlayerFactoryService {

    Player craftRandomComPlayer();

    Player craftRandomPlayer(String playerId);

    Player craftOpenPlayer(String username, String deckId);
}
