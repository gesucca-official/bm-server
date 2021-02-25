package com.gsc.bm.model.cards.bruiser;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.cards.Card;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WhatchaDoin extends AbstractCard {

    // this is the sickest shit, stealing stuff
    private Card reflectedCard;

    public WhatchaDoin() {
        super();
        setCanTarget(Set.of(CardTarget.OPPONENT));
        setCost(Map.of(Resource.VIOLENCE, 10, Resource.ALERTNESS, 10));
        setPriority(2);
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
        g.getPendingMoveOfTargetIfMovesAfterPlayer(m.getPlayerId(), m.getTargetId())
                .ifPresent(move -> {
                    this.reflectedCard = g.getCardFromHand(move.getPlayerId(), move.getPlayedCardName());
                    move.setVoid(true);
                });
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return null;
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        if (reflectedCard != null) {
            List<String> result = new ArrayList<>(List.of("Countered Target's own Move"));
            List<String> reflectedCardEffect = ((AbstractCard) reflectedCard).applyEffectOnTarget(self, target);
            if (reflectedCardEffect != null && !reflectedCardEffect.get(0).startsWith("You were too slow ")) // TODO come on gesuccaaaaa
                result.addAll(reflectedCardEffect);
            return result;
        } else
            return List.of("You were too slow and didn't countered anything.");
    }
}
