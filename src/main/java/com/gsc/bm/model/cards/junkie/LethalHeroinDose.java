package com.gsc.bm.model.cards.junkie;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;

import java.util.List;
import java.util.Set;

public class LethalHeroinDose extends AbstractCard {

    public LethalHeroinDose() {
        super();
        setCanTarget(Set.of(CardTarget.SELF));
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return List.of(
                self.getResources().get(Resource.TOXICITY) >= 30
                        ? self.loseResource(Resource.HEALTH, self.getResources().get(Resource.TOXICITY))
                        : "You didn't die from OverDose",
                self.gainResource(Resource.TOXICITY, self.getResources().get(Resource.TOXICITY) - 30)
        );
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return null;
    }
}
