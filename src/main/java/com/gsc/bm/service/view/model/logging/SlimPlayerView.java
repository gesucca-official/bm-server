package com.gsc.bm.service.view.model.logging;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
public class SlimPlayerView implements Serializable {
    private final String playerId;
    private final SlimCharacterView character;
    private final List<String> cardsInHand;
    private final List<String> deck;
}
