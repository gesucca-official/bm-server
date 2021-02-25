package com.gsc.bm.service.view.model.logging;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class SlimGameView implements Serializable {
    private final String gameId;
    private final List<SlimPlayerView> players;
    private final List<SlimMoveView> resolvedMoves;
    private final Map<String, List<String>> timeBasedEffects;
    private final boolean over;
    private final String winner;
}
