package com.gsc.bm.model.cards.bruiser.character;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;

public class BigBadBruiser extends Character {

    public BigBadBruiser() {
        super(100, 20, 1);
        getResources().put(Resource.VIOLENCE, 25);
    }

}