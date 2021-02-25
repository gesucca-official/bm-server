package com.gsc.bm.service.factories;

import com.gsc.bm.model.cards.bruiser.character.BigBadBruiser;
import com.gsc.bm.model.cards.junkie.character.ToxicJunkie;
import com.gsc.bm.model.game.ComPlayer;
import com.gsc.bm.model.game.Player;
import com.gsc.bm.model.Character;
import com.gsc.bm.repo.external.UserDeckRecordKey;
import com.gsc.bm.repo.external.UserDecksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class PlayerFactoryServiceImpl implements PlayerFactoryService {

    private final DeckFactoryService deckFactoryService;
    private final CharacterFactoryService characterFactoryService;
    private final UserDecksRepository userDecksRepo;

    @Autowired
    public PlayerFactoryServiceImpl(DeckFactoryService deckFactoryService, CharacterFactoryService characterFactoryService, UserDecksRepository userDecksRepo) {
        this.deckFactoryService = deckFactoryService;
        this.characterFactoryService = characterFactoryService;
        this.userDecksRepo = userDecksRepo;
    }

    @Override
    public Player craftRandomComPlayer() {
        Character chosenChar = randomPlayer();
        return new ComPlayer(chosenChar,
                deckFactoryService.craftBasicActionStarterCard(chosenChar),
                deckFactoryService.craftCharacterBoundStarterCards(chosenChar),
                deckFactoryService.craftLastResortStarterCard(chosenChar),
                deckFactoryService.craftCharacterStarterDeck(chosenChar.getClass().getName()));
    }

    @Override
    public Player craftRandomPlayer(String playerId) {
        Character chosenChar = randomPlayer();
        return new Player(playerId,
                chosenChar,
                deckFactoryService.craftBasicActionStarterCard(chosenChar),
                deckFactoryService.craftCharacterBoundStarterCards(chosenChar),
                deckFactoryService.craftLastResortStarterCard(chosenChar),
                deckFactoryService.craftCharacterStarterDeck(chosenChar.getClass().getName()));
    }

    @Override
    public Player craftOpenPlayer(String username, String deckId) {
        AtomicReference<Player> player = new AtomicReference<>();
        userDecksRepo.findById(new UserDeckRecordKey(username, deckId))
                .ifPresentOrElse(d -> player.set(new Player(
                        username,
                        characterFactoryService.craftCharacter(d.getDeck().getCharacterClazz()),
                        deckFactoryService.craftBasicActionOpenCard(username, deckId),
                        deckFactoryService.craftCharacterBoundOpenCards(username, deckId),
                        deckFactoryService.craftLastResortOpenCard(username, deckId),
                        deckFactoryService.craftCharacterOpenDeck(username, deckId)
                )), () -> {
                    throw new ValueNotFoundException(username + "/" + deckId);
                });
        return player.get();
    }

    private Character randomPlayer() {
        int random = (int) ((Math.random() * 100) % 2);
        if (random == 1)
            return new BigBadBruiser();
        else return new ToxicJunkie();
    }
}
