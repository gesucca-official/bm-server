package com.gsc.bm.service.factories;

import com.gsc.bm.model.Character;
import com.gsc.bm.service.view.model.deck.CharacterCardView;

public interface CharacterFactoryService {

    Character craftCharacter(String characterClazz);

    CharacterCardView craftCharacterView(String characterClazz);
}
