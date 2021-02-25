package com.gsc.bm.model.cards.junkie.character;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCharacterBoundCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;

import java.util.List;
import java.util.Set;

public class RottenSmile extends AbstractCharacterBoundCard {

    public RottenSmile() {
        super(ToxicJunkie.class);
        setCanTarget(Set.of(CardTarget.OPPONENT));
        setPriority(2);
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return List.of(
                self.gainResource(Resource.ALERTNESS, 5)
        );
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return List.of(
                target.loseResource(Resource.ALERTNESS, 5)
        );
    }
}
