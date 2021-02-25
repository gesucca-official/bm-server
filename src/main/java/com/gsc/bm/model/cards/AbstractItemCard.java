package com.gsc.bm.model.cards;

import com.gsc.bm.model.Character;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractItemCard extends AbstractCard {

    public AbstractItemCard() {
        super();
        setItem(true);
        setPriority(1);
        setCanTarget(Set.of(CardTarget.SELF));
        setCost(Map.of());
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return null;
    }
}
