package com.gsc.bm.service.session;

import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.IllegalMoveException;
import com.gsc.bm.model.game.Move;

public interface GameSessionService {

    String newGame(Game game);

    Game getGame(String gameId);

    void leaveGame(String gameId, String playerId);

    void submitMoveToGame(Move move, Runnable callback) throws IllegalMoveException;
}
