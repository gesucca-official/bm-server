package com.gsc.bm.model.cards.bruiser;

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

public class BreakYourToy extends AbstractCard {

    Card targetItem;

    public BreakYourToy() {
        super();
        setCanTarget(Set.of(CardTarget.FAR_ITEM));
        setCost(Map.of(Resource.VIOLENCE, 10));
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
        try {
            targetItem = g.getItem(m.getTargetId(), m.getChoices().get(Move.AdditionalAction.TARGET_ITEM));
        } catch (IllegalMoveException e) {
            targetItem = null;
        }
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return null;
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        if (targetItem != null) {
            target.getItems().remove(targetItem);
            return List.of("Destroyed " + targetItem.getName());
        } else
            return List.of("You were too slow and didn't break anything");
    }
}
