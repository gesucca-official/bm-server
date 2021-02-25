package com.gsc.bm.model.cards.com;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.cards.Card;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.List;
import java.util.Queue;
import java.util.Set;

public class ShuffleFeet extends AbstractCard {

    public ShuffleFeet() {
        setCanTarget(Set.of(CardTarget.OPPONENT));
        setPriority(-1);
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return null;
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        Queue<Card> targetItems = new CircularFifoQueue<>(target.getItemsSize());
        targetItems.addAll(target.getItems());
        Queue<Card> myItems = new CircularFifoQueue<>(self.getItemsSize());
        myItems.addAll(self.getItems());
        target.getItems().clear();
        target.getItems().addAll(myItems);
        self.getItems().clear();
        self.getItems().addAll(targetItems);
        return List.of(
                "Switched Place with Target, Items that were near Him now are near You"
        );
    }
}
