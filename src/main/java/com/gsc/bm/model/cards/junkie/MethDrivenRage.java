package com.gsc.bm.model.cards.junkie;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.Damage;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethDrivenRage extends AbstractCard {

    public MethDrivenRage() {
        super();
        setCost(Map.of(Resource.TOXICITY, 5));
        setCanTarget(Set.of(CardTarget.OPPONENT));
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {
    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return List.of(
                self.inflictDamage(self, new Damage(Damage.DamageType.HIT, 5))
        );
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return List.of(
                self.inflictDamage(target, new Damage(Damage.DamageType.HIT, 15))
        );
    }
}
