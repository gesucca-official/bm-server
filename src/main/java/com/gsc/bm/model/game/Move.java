package com.gsc.bm.model.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.Card;
import com.gsc.bm.service.view.model.logging.SlimMoveView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@ToString
public class Move implements Serializable {

    public enum AdditionalAction {
        DISCARD_ONE,
        TARGET_ITEM
    }

    @JsonIgnore
    public static Move voidMove(String playerId) {
        return new Move(playerId);
    }

    private final String playedCardName;
    private final String playerId;
    private final String targetId;
    private final String gameId;

    private final Map<AdditionalAction, String> choices;

    @Setter
    private boolean isVoid;

    private final Map<Card.CardTarget, List<String>> moveReport = new HashMap<>();

    @JsonCreator
    public Move(String playedCardName,
                String playerId,
                String targetId,
                String gameId,
                Map<AdditionalAction, String> choices) {
        this.playedCardName = playedCardName;
        this.playerId = playerId;
        this.targetId = targetId;
        this.gameId = gameId;
        this.choices = choices;
        this.isVoid = false;
    }

    private Move(String playerId) {
        this.playedCardName = null;
        this.playerId = playerId;
        this.targetId = null;
        this.gameId = null;
        this.choices = null;
        this.isVoid = true;
    }

    @AllArgsConstructor
    @Getter
    static class MoveCheckResult {
        boolean valid;
        String comment;
    }

    @JsonIgnore
    public MoveCheckResult isValidFor(Game game) {
        if (this.isVoid)
            return new MoveCheckResult(true, "is empty and you do nothing");

        try { // TODO for the love of god...
            // check if the discarded card is valid if character bound move
            if (game.getCardFromHand(playerId, playedCardName).isCharacterBound()) {
                if (choices == null || choices.isEmpty())
                    return new MoveCheckResult(false, "character bound cards should discard something else");
                Card toBeDiscarded = game.getCardFromHand(playerId, choices.get(AdditionalAction.DISCARD_ONE));
                if (toBeDiscarded.isCharacterBound())
                    return new MoveCheckResult(false, "can't discard character bound card");
                if (toBeDiscarded.isBasicAction())
                    return new MoveCheckResult(false, "can't discard basic action card");
                if (toBeDiscarded.isLastResort())
                    return new MoveCheckResult(false, "can't discard last resort card");
            }

            // check if target object is valid
            if (game.getCardFromHand(playerId, playedCardName).getCanTarget().contains(Card.CardTarget.NEAR_ITEM)) {
                if (choices == null || choices.isEmpty())
                    return new MoveCheckResult(false, "cards that use items should explicit which one is targeted");
                // this already throws IllegalMoveException if item is not present
                game.getItem(playerId, choices.get(AdditionalAction.TARGET_ITEM));
            }
            if (game.getCardFromHand(playerId, playedCardName).getCanTarget().contains(Card.CardTarget.FAR_ITEM)) {
                if (choices == null || choices.isEmpty())
                    return new MoveCheckResult(false, "cards that use items should explicit which one is targeted");
                // as above
                game.getItem(targetId, choices.get(AdditionalAction.TARGET_ITEM));
            }

            // check if player has already submitted a move
            if (game.getPendingMoves().stream().anyMatch(m -> m.getPlayerId().equalsIgnoreCase(playerId)))
                return new MoveCheckResult(false, "already submitted a move");
            // getting this card also checks if it is in that player's hand
            Card playedCard = game.getCardFromHand(playerId, playedCardName);
            Map<Resource, Integer> playerResources = game.getPlayers().get(playerId).getCharacter().getResources();

            // check if costs can be satisfied
            for (Map.Entry<Resource, Integer> cost : playedCard.getCost().entrySet()) {
                int availableResource = playerResources.getOrDefault(cost.getKey(), 0);
                if (availableResource < cost.getValue())
                    return new MoveCheckResult(false, "cant be cast with current resources");
            }
        } catch (IllegalMoveException e) {
            return new MoveCheckResult(false, e.getWhatHeDid());
        }
        return new MoveCheckResult(true, "good, cast it");
    }

    @JsonIgnore
    public void applyCostTo(Game game) {
        if (this.isVoid)
            return;
        game.getCardFromHand(playerId, playedCardName).getCost().forEach(
                (key, value) -> game.getSelf(this).getCharacter().loseResource(key, value)
        );
    }

    @JsonIgnore
    public void applyEffectTo(Game game) {
        if (this.isVoid) {
            moveReport.put(Card.CardTarget.SELF, List.of("Didn't do anything!"));
            return;
        }
        moveReport.putAll(
                game.getCardFromHand(playerId, playedCardName).resolve(game, this)
        );
    }

    @JsonIgnore
    public SlimMoveView getSlimView() {
        return SlimMoveView.builder()
                .playerId(playerId)
                .targetId(targetId)
                .playedCardName(playedCardName)
                .choices(choices)
                .moveReport(moveReport)
                .build();
    }

}
