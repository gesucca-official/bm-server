package com.gsc.bm.model.cards.junkie;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;
import com.gsc.bm.model.game.Timer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class BadTrip extends AbstractCard {

    public BadTrip() {
        super();
        setCanTarget(Set.of(CardTarget.SELF));
        setCost(Map.of(Resource.ALERTNESS, 10));
        setPriority(2);
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        self.getImmunities().remove(Resource.TOXICITY);
        self.getTimers().add(new Timer("Bad Trip", c -> c.getImmunities().add(Resource.TOXICITY), 3));

        return List.of(
                "Lost Immunity to Toxicity for 3 Turns",
                self.gainResource(Resource.TOXICITY, self.getResources().get(Resource.TOXICITY))
        );
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return null;
    }
}
