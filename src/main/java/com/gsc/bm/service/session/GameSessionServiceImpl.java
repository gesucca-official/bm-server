package com.gsc.bm.service.session;

import com.gsc.bm.model.game.*;
import com.gsc.bm.service.session.model.ActionLog;
import com.gsc.bm.service.view.ViewExtractorService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Log4j2
public class GameSessionServiceImpl implements GameSessionService {

    private static final Map<Game, Set<String>> _GAMES = new ConcurrentHashMap<>();

    private final ConnectionsService connectionsService;
    private final GameLoggingService gameLoggingService;
    private final ViewExtractorService viewExtractorService;

    @Autowired
    public GameSessionServiceImpl(ConnectionsService connectionsService,
                                  GameLoggingService gameLoggingService,
                                  ViewExtractorService viewExtractorService) {
        this.connectionsService = connectionsService;
        this.gameLoggingService = gameLoggingService;
        this.viewExtractorService = viewExtractorService;
    }

    @Override
    public synchronized String newGame(Game game) {
        // TODO this is an assumption: what happens if one player queued closes the game?
        Set<String> usersSubscribed = game.getPlayers().values()
                .stream()
                .filter(p -> !(p instanceof ComPlayer))
                .map(Player::getPlayerId)
                .peek(p -> connectionsService.userActivityChanged(
                        p, "Playing against "
                                + game.getOpponents(p).stream().map(Player::getPlayerId).collect(Collectors.joining(","))))
                .collect(Collectors.toSet());

        _GAMES.put(game, usersSubscribed);
        log.info("Created Game " + game.getGameId() + " with users subscribed: " + usersSubscribed);
        gameLoggingService.log(
                game, GameLoggingService.STARTED,
                new ActionLog("Created Game", viewExtractorService.extractGlobalSlimView(game)));

        return game.getGameId(); // return the id just for convenience
    }

    @Override
    public synchronized Game getGame(String gameId) {
        for (Game g : _GAMES.keySet())
            if (g.getGameId().equals(gameId))
                return g;
        throw new IllegalArgumentException(gameId);
    }

    @Override
    public synchronized void leaveGame(String gameId, String playerId) {
        _GAMES.get(getGame(gameId)).remove(playerId);
        connectionsService.userActivityChanged(playerId, "Free");
        log.info("Player " + playerId + " left Game " + gameId);

        if (_GAMES.get(getGame(gameId)).isEmpty()) {
            gameLoggingService.flush(getGame(gameId));
            log.info("No one is anymore subscribed to Game " + gameId + " - removed!");
            _GAMES.remove(getGame(gameId));
        }
    }

    @Override
    public synchronized void submitMoveToGame(Move move, Runnable callback) throws IllegalMoveException {
        gameLoggingService.log(
                getGame(move.getGameId()), GameLoggingService.IN_PROGRESS,
                new ActionLog("Human Player " + move.getPlayerId() + " submitted a Move", move)
        );
        getGame(move.getGameId()).submitMove(move);

        if (getGame(move.getGameId()).isReadyToResolveMoves()) {
            getGame(move.getGameId()).resolveMoves(
                    (game) -> {
                        gameLoggingService.log(
                                getGame(move.getGameId()), GameLoggingService.IN_PROGRESS,
                                new ActionLog("Game Updated after Moves Resolutions Chain",
                                        viewExtractorService.extractGlobalSlimView(game))
                        );
                        callback.run();
                    },
                    (turn, loggedEventsList) -> {
                        gameLoggingService.log(
                                getGame(move.getGameId()), GameLoggingService.IN_PROGRESS,
                                new ActionLog("Turn " + turn + " internal Game Logs", loggedEventsList)
                        );
                        loggedEventsList.clear();
                    });
        }
    }

}
