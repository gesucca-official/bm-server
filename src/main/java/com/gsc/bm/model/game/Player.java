package com.gsc.bm.model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.Card;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;

@Getter
public class Player implements Serializable {

    public static final int CARDS_IN_HAND = 6;

    private final String playerId;
    private final Character character;

    private final List<Card> cardsInHand = new ArrayList<>();
    private final Stack<Card> deck = new Stack<>();

    private final Card lastResortCard;

    public Player(String id, Character character,
                  Card basicActionCard, List<Card> characterBoundCards, Card lastResortCard,
                  List<Card> deck) {
        this.playerId = id;
        this.character = character;

        this.cardsInHand.add(basicActionCard);
        this.cardsInHand.addAll(characterBoundCards);
        this.lastResortCard = lastResortCard;

        this.deck.addAll(deck);
        Collections.shuffle(this.deck);
        drawCards(CARDS_IN_HAND - cardsInHand.size());
    }

    @JsonProperty
    public int getDeckSize() {
        return deck.size();
    }

    public void drawCard() {
        if (!deck.isEmpty()) {
            Card card = deck.pop();
            if (card.isItem()) {
                character.getItems().add(card);
                drawCard();
            } else
                cardsInHand.add(card);
        } else if (!cardsInHand.contains(lastResortCard))
            cardsInHand.add(lastResortCard);
    }

    public void drawCards(int howMany) {
        for (int i = 0; i < howMany; i++)
            drawCard();
    }

    public void discardCard(Card card) {
        if (!card.isCharacterBound() && !card.isBasicAction())
            cardsInHand.remove(card);
    }

    @JsonIgnore
    public Map<Resource, Integer> getResources() {
        return character.getResources();
    }

}
