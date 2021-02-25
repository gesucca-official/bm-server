package com.gsc.bm.model.cards;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;
import com.gsc.bm.service.factories.CardFactoryService;

import java.util.List;
import java.util.Map;

public abstract class AbstractCharacterBoundCard extends AbstractCard {

    public AbstractCharacterBoundCard(Class<? extends Character> boundTo) {
        super();
        setBoundToCharacter(boundTo.getName().replace(CardFactoryService.BASE_CARDS_PKG, ""));
    }

    @Override
    public Map<CardTarget, List<String>> resolve(Game game, Move move) {
        Card toBeDiscarded = game.getCardFromHand(
                move.getPlayerId(),
                move.getChoices().get(Move.AdditionalAction.DISCARD_ONE));
        game.getSelf(move).discardCard(toBeDiscarded);
        return super.resolve(game, move);
    }

}
