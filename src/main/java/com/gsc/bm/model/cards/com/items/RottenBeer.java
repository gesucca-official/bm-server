package com.gsc.bm.model.cards.com.items;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractItemCard;
import com.gsc.bm.model.cards.com.tokens.BrokenBeerBottle;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;

import java.util.List;

public class RottenBeer extends AbstractItemCard {

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
        g.getSelf(m).getCharacter().getStatuses().add(BrokenBeerBottle.BROKEN_BEER_STATUS);
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return List.of(
                "Gained equip status: BROKEN BEER",
                self.gainResource(Resource.ALCOHOL, 5)
        );
    }

}
