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

public class ToxicBloodDrain extends AbstractCard {

    int toxicity;

    public ToxicBloodDrain() {
        super();
        setCanTarget(Set.of(CardTarget.OPPONENT));
        setCost(Map.of(Resource.TOXICITY, 10));
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
        toxicity = g.getSelf(m).getCharacter().getResources().get(Resource.TOXICITY);
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return List.of(
                self.loseResource(Resource.TOXICITY, toxicity)
        );
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        target.getImmunities().remove(Resource.TOXICITY);
        self.getTimers().add(new Timer("Blood Drain", c -> c.getImmunities().add(Resource.TOXICITY), 1));
        return List.of(
                target.gainResource(Resource.TOXICITY, toxicity)
        );
    }
}
