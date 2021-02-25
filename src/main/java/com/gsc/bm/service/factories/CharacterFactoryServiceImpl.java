package com.gsc.bm.service.factories;

import com.gsc.bm.model.Character;
import com.gsc.bm.service.view.ViewExtractorService;
import com.gsc.bm.service.view.model.deck.CharacterCardView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CharacterFactoryServiceImpl implements CharacterFactoryService {

    private final ViewExtractorService viewService;

    @Autowired
    public CharacterFactoryServiceImpl(ViewExtractorService viewService) {
        this.viewService = viewService;
    }

    @Override
    public Character craftCharacter(String characterClazz) {
        try {
            return (Character) Class.forName(CardFactoryService.BASE_CARDS_PKG + characterClazz).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // this should really be managed better
        }
    }

    @Override
    public CharacterCardView craftCharacterView(String characterClazz) {
        return viewService.extractDeckBuildingView(craftCharacter(characterClazz));
    }

}
