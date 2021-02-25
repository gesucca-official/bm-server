package com.gsc.bm.service.view.model.client;

import com.gsc.bm.model.game.Move;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Builder
@Getter
public class ClientGameView {
    private final String gameId;
    private final Map<String, AbstractClientPlayerView> players;
    private final List<Move> resolvedMoves;
    private final Map<String, List<String>> timeBasedEffects;
    private final boolean over;
    private final String winner;
}
