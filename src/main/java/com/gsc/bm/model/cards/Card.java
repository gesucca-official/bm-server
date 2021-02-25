package com.gsc.bm.model.cards;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonDeserialize(as = Card.DeserializedCard.class)
public interface Card {

    enum CardTarget {
        SELF, OPPONENT, NEAR_ITEM, FAR_ITEM
    }

    String getName();

    String getBindingName();

    String getEffect();

    boolean isItem();

    boolean isBasicAction();

    boolean isLastResort();

    boolean isCharacterBound();

    String getBoundToCharacter();

    int getPriority();  // higher priority resolved first (2 before 1)

    Set<CardTarget> getCanTarget();

    Map<Resource, Integer> getCost();

    Map<CardTarget, List<String>> resolve(Game game, Move move);

    @Getter
    class DeserializedCard implements Card {
        String name;
        String bindingName;
        String effect;
        boolean item;
        boolean basicAction;
        boolean lastResort;
        boolean characterBound;
        String boundToCharacter;
        int priority;
        Set<CardTarget> canTarget;
        Map<Resource, Integer> cost;

        @Override
        public Map<CardTarget, List<String>> resolve(Game game, Move move) {
            return null;
        }
    }

}
