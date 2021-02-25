package com.gsc.bm.model.cards.junkie;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;
import com.gsc.bm.model.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AmphetamineReflexes extends AbstractCard {

    private final List<String> countered = new ArrayList<>();

    public AmphetamineReflexes() {
        super();
        setCanTarget(Set.of(CardTarget.SELF));
        setCost(Map.of(Resource.TOXICITY, 10));
        setPriority(2);
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
        for (Player oppo : g.getOpponents(m.getPlayerId()))
            g.getPendingMoveOfTargetIfMovesAfterPlayer(m.getPlayerId(), oppo.getPlayerId())
                    .ifPresent(move -> {
                        if (move.getTargetId().equals(m.getPlayerId())) {
                            countered.add(move.getPlayerId());
                            move.setVoid(true);
                        }
                    });
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return null;
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        if (!countered.isEmpty())
            return List.of("Countered Move(s) of: " + countered);
        else
            return List.of("You didn't countered anything.");
    }

}
