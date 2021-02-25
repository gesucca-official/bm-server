package com.gsc.bm.model.game;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.cards.Card;

import java.util.*;
import java.util.stream.Collectors;

public class ComPlayer extends Player {

    // for now this fabulous AI is just random choices

    public ComPlayer(Character character, Card basicActionCard, List<Card> characterBoundCards, Card lastResortCard, List<Card> deck) {
        super("ComPlayer_" + (int) (Math.random() * 10000), character, basicActionCard, characterBoundCards, lastResortCard, deck);
    }

    public Move chooseMove(Game game) {
        List<Card> availableCards = new ArrayList<>(getCardsInHand());
        Collections.shuffle(availableCards);
        game.getLoggedTurnEvents().add(this.getPlayerId() + " is choosing a move among these cards: "
                + availableCards.stream().map(Card::getName).collect(Collectors.joining(",")));

        for (int i = availableCards.size() - 1; i >= 0; i--) {
            Move chosenMove = craftMoveByIndex(
                    game, availableCards, i, choseTarget(game, availableCards.get(i)), game.getGameId());
            game.getLoggedTurnEvents().add(this.getPlayerId() + " is testing this Move: " + chosenMove);
            if (chosenMove.isValidFor(game).isValid())
                return chosenMove;
        }

        // if no move in hand are valid, return void move
        return Move.voidMove(getPlayerId());
    }

    private String choseTarget(Game game, Card chosenCard) {
        // TODO work this out: cards maybe will be able to targets multiple things at once (ex. you or opponents)
        // this will break
        if (chosenCard.getCanTarget().contains(Card.CardTarget.SELF)
                || chosenCard.getCanTarget().contains(Card.CardTarget.NEAR_ITEM))
            return "SELF";

        List<String> availableTargets = game.getOpponents(getPlayerId())
                .stream()
                .filter(t -> {
                    if (chosenCard.getCanTarget().contains(Card.CardTarget.FAR_ITEM))
                        return t.getCharacter().getItems().size() > 0;
                    else return true;
                })
                .map(Player::getPlayerId)
                .collect(Collectors.toList());

        Collections.shuffle(availableTargets);
        return availableTargets.size() > 0 ? availableTargets.get(0) : "INVALID_TARGET_TEST_ANOTHER_ONE";
    }

    private Move craftMoveByIndex(Game game, List<Card> cards, int index, String target, String gameId) {
        Map<Move.AdditionalAction, String> choices = new HashMap<>();
        if (cards.get(index).isCharacterBound())
            choices.put(Move.AdditionalAction.DISCARD_ONE, chooseDiscard(new ArrayList<>(cards)));
        if (cards.get(index).getCanTarget().contains(Card.CardTarget.NEAR_ITEM))
            choices.put(Move.AdditionalAction.TARGET_ITEM, choseNearItem());
        else if (cards.get(index).getCanTarget().contains(Card.CardTarget.FAR_ITEM))
            choices.put(Move.AdditionalAction.TARGET_ITEM, choseFarItem(game, target));

        return new Move(
                cards.get(index).getName(),
                getPlayerId(),
                target,
                gameId,
                choices
        );
    }

    private String choseFarItem(Game game, String targetPlayer) {
        List<String> items = game.getOpponents(getPlayerId())
                .stream()
                .filter(p -> p.getPlayerId().equals(targetPlayer))
                .flatMap(player -> player.getCharacter().getItems().stream())
                .map(Card::getName)
                .collect(Collectors.toList());
        Collections.shuffle(items);
        return items.size() > 0 ? items.get(0) : null;
    }

    private String choseNearItem() {
        List<Card> items = new ArrayList<>(getCharacter().getItems());
        Collections.shuffle(items);
        return items.size() > 0 ? items.get(0).getName() : null;
    }

    private String chooseDiscard(List<Card> cards) {
        Collections.shuffle(cards); // this was shuffling the original card list on which I was iterating on lol
        for (Card c : cards)
            if (!c.isCharacterBound() && !c.isBasicAction() && !c.isLastResort())
                return c.getName();
        return null;
    }

}
