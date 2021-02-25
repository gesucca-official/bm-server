package com.gsc.bm.model.cards.bitch;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;

import java.util.List;

public class SoberUp extends AbstractCard {

    public SoberUp() {
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        int howMuch = self.getResources().get(Resource.ALCOHOL);
        return List.of(
                self.loseResource(Resource.ALCOHOL, howMuch),
                self.gainResource(Resource.HEALTH, howMuch / 2)
        );
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return null;
    }
}
