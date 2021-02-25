package com.gsc.bm.model.cards.com.items;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractItemCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;

import java.util.List;

public class CocktailOnTheGround extends AbstractItemCard {

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return List.of(
                self.gainResource(Resource.VIOLENCE, 20),
                self.gainResource(Resource.ALCOHOL, 15),
                self.gainResource(Resource.TOXICITY, 5)
        );
    }

}