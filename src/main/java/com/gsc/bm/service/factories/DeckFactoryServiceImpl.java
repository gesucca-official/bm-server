package com.gsc.bm.service.factories;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.cards.Card;
import com.gsc.bm.repo.external.UserDeckRecordKey;
import com.gsc.bm.repo.external.UserDecksRepository;
import com.gsc.bm.repo.internal.StarterDeckBasicCardsRecord;
import com.gsc.bm.repo.internal.StarterDeckBasicCardsRepository;
import com.gsc.bm.repo.internal.StarterDeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DeckFactoryServiceImpl implements DeckFactoryService {

    private final StarterDeckRepository starterDeckRepo;
    private final StarterDeckBasicCardsRepository starterDeckBasicCardsRepo;
    private final UserDecksRepository userDecksRepo;
    private final CardFactoryService cardFactoryService;

    @Autowired
    public DeckFactoryServiceImpl(StarterDeckRepository starterDeckRepo,
                                  StarterDeckBasicCardsRepository starterDeckBasicCardsRepo,
                                  UserDecksRepository userDecksRepo,
                                  CardFactoryService cardFactoryService) {
        this.starterDeckRepo = starterDeckRepo;
        this.starterDeckBasicCardsRepo = starterDeckBasicCardsRepo;
        this.userDecksRepo = userDecksRepo;
        this.cardFactoryService = cardFactoryService;
    }

    // TODO some code can be factored out of these methods but I'm lazy

    @Override
    public List<Card> craftCharacterStarterDeck(String pgClazz) {
        return starterDeckRepo.findAllByPgClazz(pgClazz.replace(CardFactoryService.BASE_CARDS_PKG, ""))
                .stream()
                .map(rec -> {
                    List<String> copies = new ArrayList<>(rec.getQty());
                    IntStream.range(0, rec.getQty()).forEach(n -> copies.add(rec.getCardClazz()));
                    return copies;
                })
                .flatMap(Collection::stream)
                .map(cardFactoryService::craftCard)
                .collect(Collectors.toList());
    }

    @Override
    public Card craftBasicActionStarterCard(Character character) {
        Optional<StarterDeckBasicCardsRecord> basics = starterDeckBasicCardsRepo.findById(
                character.getClass().getName().replace(CardFactoryService.BASE_CARDS_PKG, ""));
        if (basics.isPresent())
            return cardFactoryService.craftCard(basics.get().getBasicClazz());
        else
            throw new ValueNotFoundException(character.getClass().getName().replace(CardFactoryService.BASE_CARDS_PKG, ""));
    }

    @Override
    public List<Card> craftCharacterBoundStarterCards(Character character) {
        Optional<StarterDeckBasicCardsRecord> basics = starterDeckBasicCardsRepo.findById(
                character.getClass().getName().replace(CardFactoryService.BASE_CARDS_PKG, ""));
        if (basics.isPresent())
            return List.of(
                    cardFactoryService.craftCard(basics.get().getChBoundClazz1()),
                    cardFactoryService.craftCard(basics.get().getChBoundClazz2())
            );
        else
            throw new ValueNotFoundException(character.getClass().getName().replace(CardFactoryService.BASE_CARDS_PKG, ""));

    }

    @Override
    public Card craftLastResortStarterCard(Character character) {
        Optional<StarterDeckBasicCardsRecord> basics = starterDeckBasicCardsRepo.findById(
                character.getClass().getName().replace(CardFactoryService.BASE_CARDS_PKG, ""));
        if (basics.isPresent())
            return cardFactoryService.craftCard(basics.get().getLastResortClazz());
        else
            throw new ValueNotFoundException(character.getClass().getName().replace(CardFactoryService.BASE_CARDS_PKG, ""));
    }

    @Override
    public List<Card> craftCharacterOpenDeck(String username, String deckId) {
        List<Card> craftedDeck = new ArrayList<>(20);
        userDecksRepo.findById(new UserDeckRecordKey(username, deckId))
                .ifPresentOrElse(
                        deck -> deck.getDeck().getRegularCardsClazz()
                                .stream()
                                .map(cardFactoryService::craftCard)
                                .forEach(craftedDeck::add)
                        , () -> {
                            throw new ValueNotFoundException(username + "/" + deckId);
                        });
        return craftedDeck;
    }

    @Override
    public Card craftBasicActionOpenCard(String username, String deckId) {
        AtomicReference<Card> card = new AtomicReference<>();
        userDecksRepo.findById(new UserDeckRecordKey(username, deckId))
                .ifPresentOrElse(
                        deck -> card.set(cardFactoryService.craftCard(deck.getDeck().getBasicActionCardClazz()))
                        , () -> {
                            throw new ValueNotFoundException(username + "/" + deckId);
                        });
        return card.get();
    }

    @Override
    public List<Card> craftCharacterBoundOpenCards(String username, String deckId) {
        List<Card> craftedDeck = new ArrayList<>(20);
        userDecksRepo.findById(new UserDeckRecordKey(username, deckId))
                .ifPresentOrElse(
                        deck -> deck.getDeck().getCharacterBoundCardsClazz()
                                .stream()
                                .map(cardFactoryService::craftCard)
                                .forEach(craftedDeck::add)
                        , () -> {
                            throw new ValueNotFoundException(username + "/" + deckId);
                        });
        return craftedDeck;
    }

    @Override
    public Card craftLastResortOpenCard(String username, String deckId) {
        AtomicReference<Card> card = new AtomicReference<>();
        userDecksRepo.findById(new UserDeckRecordKey(username, deckId))
                .ifPresentOrElse(
                        deck -> card.set(cardFactoryService.craftCard(deck.getDeck().getLastResortCardClazz()))
                        , () -> {
                            throw new ValueNotFoundException(username + "/" + deckId);
                        });
        return card.get();
    }

}
