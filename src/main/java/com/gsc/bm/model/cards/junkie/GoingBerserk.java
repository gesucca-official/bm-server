package com.gsc.bm.model.cards.junkie;

import com.gsc.bm.model.Damage;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.Character;
import com.gsc.bm.model.cards.AbstractCard;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GoingBerserk extends AbstractCard {

    public GoingBerserk() {
        super();
        setCanTarget(Set.of(CardTarget.OPPONENT));
        setCost(Map.of(Resource.TOXICITY, 10));
        setCost(Map.of(Resource.ALCOHOL, 10));
    }

    @Override
    public void applyOtherUnfathomableLogic(Game g, Move m) {

    }

    @Override
    public List<String> applyEffectOnSelf(Character self) {
        return null;
    }

    @Override
    public List<String> applyEffectOnTarget(Character self, Character target) {
        return List.of(
                self.inflictDamage(target, new Damage(Damage.DamageType.HIT, 30))
        );
    }
}
