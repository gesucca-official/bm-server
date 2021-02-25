package com.gsc.bm.service.view;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.service.view.model.client.ClientGameView;
import com.gsc.bm.service.view.model.deck.CharacterCardView;
import com.gsc.bm.service.view.model.logging.SlimGameView;

public interface ViewExtractorService {

    SlimGameView extractGlobalSlimView(Game game);

    ClientGameView extractViewFor(Game game, String playerId);

    CharacterCardView extractDeckBuildingView(Character character);

}
