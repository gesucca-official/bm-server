package com.gsc.bm.service.view.model.logging;

import com.gsc.bm.model.cards.Card;
import com.gsc.bm.model.game.Move;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class SlimMoveView implements Serializable {
    private final String playedCardName;
    private final String playerId;
    private final String targetId;
    private final Map<Move.AdditionalAction, String> choices;
    private final Map<Card.CardTarget, List<String>> moveReport;
}
