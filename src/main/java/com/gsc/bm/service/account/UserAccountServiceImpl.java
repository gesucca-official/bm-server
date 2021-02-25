package com.gsc.bm.service.account;

import com.gsc.bm.model.cards.Card;
import com.gsc.bm.repo.external.*;
import com.gsc.bm.repo.internal.CardsGuiRepository;
import com.gsc.bm.repo.internal.CharactersGuiRepository;
import com.gsc.bm.service.account.model.UserAccountInfo;
import com.gsc.bm.service.account.model.UserCollection;
import com.gsc.bm.service.account.model.UserGuiDeck;
import com.gsc.bm.service.factories.CardFactoryService;
import com.gsc.bm.service.factories.CharacterFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserCredentialsRepository userCredentialsRepo;
    private final UserDecksRepository userDecksRepo;
    private final CardsGuiRepository cardsGuiRepo;
    private final CharactersGuiRepository charactersGuiRepo;
    private final CardFactoryService cardFactoryService;
    private final CharacterFactoryService characterFactoryService;

    @Autowired
    public UserAccountServiceImpl(UserCredentialsRepository userCredentialsRepo,
                                  UserDecksRepository userDecksRepo,
                                  CardsGuiRepository cardsGuiRepo,
                                  CharactersGuiRepository charactersGuiRepo,
                                  CardFactoryService cardFactoryService,
                                  CharacterFactoryService characterFactoryService) {
        this.userCredentialsRepo = userCredentialsRepo;
        this.userDecksRepo = userDecksRepo;
        this.cardsGuiRepo = cardsGuiRepo;
        this.charactersGuiRepo = charactersGuiRepo;
        this.cardFactoryService = cardFactoryService;
        this.characterFactoryService = characterFactoryService;
    }

    @Override
    public UserAccountInfo loadUserAccountInfo(String username) {
        Optional<UserCredentialsRecord> rec = userCredentialsRepo.findById(username);
        return rec.map(userCredentialsRecord ->
                new UserAccountInfo(
                        userCredentialsRecord.getUsername(),
                        userCredentialsRecord.getEmail(),
                        userCredentialsRecord.getRole(),
                        getUserDecks(username),
                        getUserCardCollection()))
                .orElse(null);
    }

    @Override
    public void addUserDeck(String username, UserGuiDeck deck) {
        userDecksRepo.save(
                new UserDecksRecord(username, deck.getDeckId(), fromGuiToStoredDeck(deck))
        );
    }

    @Override
    public void deleteUserDeck(String username, UserGuiDeck deck) {
        userDecksRepo.deleteById(new UserDeckRecordKey(username, deck.getDeckId()));
    }

    // for now everyone has all the cards
    private UserCollection getUserCardCollection() {
        return new UserCollection(
                charactersGuiRepo.findAll()
                        .stream()
                        .map(r -> characterFactoryService.craftCharacterView(r.getClazz()))
                        .collect(Collectors.toSet()),
                cardsGuiRepo.findAll()
                        .stream()
                        .map(cardsGuiRecord -> cardFactoryService.craftCard(cardsGuiRecord.getClazz()))
                        .collect(Collectors.toSet())
        );
    }

    private Set<UserGuiDeck> getUserDecks(String username) {
        return userDecksRepo.findAllByUsername(username)
                .stream()
                .map(d -> fromStoredToGuiDeck(d.getDeck(), d.getDeckId()))
                .collect(Collectors.toSet());
    }

    private UserGuiDeck fromStoredToGuiDeck(UserDecksRecord.UserStoredDeck deck, String deckId) {
        return new UserGuiDeck(
                deckId,
                characterFactoryService.craftCharacterView(deck.getCharacterClazz()),
                cardFactoryService.craftCard(deck.getBasicActionCardClazz()),
                cardFactoryService.craftCard(deck.getLastResortCardClazz()),
                deck.getCharacterBoundCardsClazz().stream().map(cardFactoryService::craftCard).collect(Collectors.toList()),
                deck.getRegularCardsClazz().stream().map(cardFactoryService::craftCard).collect(Collectors.toList())
        );
    }

    private UserDecksRecord.UserStoredDeck fromGuiToStoredDeck(UserGuiDeck deck) {
        return new UserDecksRecord.UserStoredDeck(
                deck.getCharacter().getBindingName(),
                deck.getBasicActionCard().getBindingName(),
                deck.getLastResortCard().getBindingName(),
                deck.getCharacterBoundCards().stream().map(Card::getBindingName).collect(Collectors.toList()),
                deck.getRegularCards().stream().map(Card::getBindingName).collect(Collectors.toList())
        );
    }
}
