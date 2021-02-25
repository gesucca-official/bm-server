package com.gsc.bm.model.cards.bruiser.character;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Damage;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCharacterBoundCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;

import java.util.List;
import java.util.Set;

public class LittleSmack extends AbstractCharacterBoundCard {

    public LittleSmack() {
        super(BigBadBruiser.class);
        setCanTarget(Set.of(CardTarget.OPPONENT));
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return List.of(
                self.gainResource(Resource.ALERTNESS, 5)
        );
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return List.of(
                self.inflictDamage(target, new Damage(Damage.DamageType.HIT, 5))
        );
    }

}
