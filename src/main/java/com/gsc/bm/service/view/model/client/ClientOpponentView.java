package com.gsc.bm.service.view.model.client;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ClientOpponentView extends AbstractClientPlayerView {
    private final String playerId;
    private final ClientCharacterView character;
    private final int cardsInHand;
    private final int deck;
}
