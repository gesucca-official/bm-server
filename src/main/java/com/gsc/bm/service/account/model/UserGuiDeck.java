package com.gsc.bm.service.account.model;

import com.gsc.bm.model.cards.Card;
import com.gsc.bm.service.view.model.deck.CharacterCardView;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class UserGuiDeck {
    String deckId;
    CharacterCardView character;
    Card basicActionCard;
    Card lastResortCard;
    List<Card> characterBoundCards;
    List<Card> regularCards;
}
