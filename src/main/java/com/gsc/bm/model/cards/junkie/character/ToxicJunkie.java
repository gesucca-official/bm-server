package com.gsc.bm.model.cards.junkie.character;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;

public class ToxicJunkie extends Character {

    public ToxicJunkie() {
        super(100, 35, 3);
        getResources().put(Resource.TOXICITY, 10);
        getImmunities().add(Resource.TOXICITY);
    }
}
