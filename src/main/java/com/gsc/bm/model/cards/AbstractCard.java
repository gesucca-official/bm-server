package com.gsc.bm.model.cards;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;
import com.gsc.bm.service.factories.CardFactoryService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter(AccessLevel.PROTECTED)
public abstract class AbstractCard implements Card, LoadableCard, Serializable {

    private boolean isItem;
    private boolean isBasicAction;
    private boolean isLastResort;

    private int priority;
    private Map<Resource, Integer> cost;
    private Set<CardTarget> canTarget;

    private String boundToCharacter = null;

    @Override
    public boolean isCharacterBound() {
        return boundToCharacter != null;
    }

    @Override
    public String getBindingName() {
        return this.getClass().getName().replace(CardFactoryService.BASE_CARDS_PKG, "");
    }

    @Override
    public Map<CardTarget, List<String>> resolve(Game g, Move m) {
        applyOtherUnfathomableLogic(g, m);
        List<String> selfReport = applyEffectOnSelf(g.getSelf(m).getCharacter());
        List<String> oppoReport = applyEffectOnTarget(g.getSelf(m).getCharacter(), g.getTarget(m).getCharacter());
        return Map.of(
                CardTarget.SELF, selfReport == null ? List.of() : selfReport,
                CardTarget.OPPONENT, oppoReport == null ? List.of() : oppoReport
        );
    }

    public abstract void applyOtherUnfathomableLogic(Game g, Move m);

    public abstract List<String> applyEffectOnSelf(Character self);

    public abstract List<String> applyEffectOnTarget(Character self, Character target);

    public AbstractCard() {
        priority = 1;
        cost = Map.of();
    }

    @Setter(AccessLevel.NONE)
    private String name;
    @Setter(AccessLevel.NONE)
    private String effect;
    @Setter(AccessLevel.NONE)
    private String image;
    @Setter(AccessLevel.NONE)
    private String sprite;

    @Override
    public void setGuiName(String name) {
        this.name = name;
    }

    @Override
    public void setGuiEffectDescription(String description) {
        this.effect = description;
    }

    @Override
    public void setGuiImage(String image) {
        this.image = image;
    }

    @Override
    public void setGuiSprite(String sprite) {
        this.sprite = sprite;
    }

    @Override
    public final boolean equals(Object o) {
        if (o instanceof AbstractCard) {
            AbstractCard e = (AbstractCard) o;
            return e.name.equals(this.name);
        } else
            return false;
    }

    protected <T> List<T> mergeList(List<T> list1, List<T> list2) {
        List<T> mergedList = new ArrayList<>(list1.size() + list2.size());
        mergedList.addAll(list1);
        mergedList.addAll(list2);
        return mergedList;
    }
}