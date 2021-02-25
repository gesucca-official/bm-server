package com.gsc.bm.model.cards.com;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.cards.Card;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.IllegalMoveException;
import com.gsc.bm.model.game.Move;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Steal extends AbstractCard {

    private String message;
    private Card targetItem;

    public Steal() {
        super();
        setCanTarget(Set.of(CardTarget.FAR_ITEM));
        setCost(Map.of(Resource.ALERTNESS, 10));
        setPriority(0);
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
        try {
            targetItem = g.getItem(m.getTargetId(), m.getChoices().get(Move.AdditionalAction.TARGET_ITEM));
            message = "Stolen " + targetItem.getName() + " from " + m.getTargetId();
        } catch (IllegalMoveException e) {
            targetItem = null;
            message = "Couldn't Steal anything!";
            return;
        }
        g.getTarget(m).getCharacter().getItems().remove(targetItem);
        g.getSelf(m).getCharacter().getItems().add(targetItem);
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return List.of(message);
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return null;
    }
}
