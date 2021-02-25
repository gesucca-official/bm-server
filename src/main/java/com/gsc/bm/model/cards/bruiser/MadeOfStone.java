package com.gsc.bm.model.cards.bruiser;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Damage;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;
import com.gsc.bm.model.game.status.Status;
import com.gsc.bm.model.game.status.StatusFlow;
import com.gsc.bm.model.game.status.StatusType;

import java.util.List;
import java.util.Set;

public class MadeOfStone extends AbstractCard {

    public MadeOfStone() {
        super();
        setPriority(0);
        setCanTarget(Set.of(CardTarget.SELF));
    }

    static Status MADE_OF_STONE = Status.builder()
            .name("MADE OF STONE")
            .description("Cut Damage Taken: x0.5")
            .type(StatusType.GOOD)
            .flow(StatusFlow.INPUT)
            .impactedProperty(Damage.DamageType.CUT)
            .amountFunction(incomingDamage -> incomingDamage * 0.5f)
            .lastsForTurns(3)
            .build();

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        self.getStatuses().add(MADE_OF_STONE);
        return List.of(
                "Gained Status: MADE OF STONE",
                self.gainResource(Resource.VIOLENCE, 10)
        );
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return null;
    }
}
