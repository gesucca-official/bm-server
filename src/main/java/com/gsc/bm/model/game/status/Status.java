package com.gsc.bm.model.game.status;

import com.gsc.bm.model.Statistic;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
public class Status implements Serializable {

    public static final Set<StatusType> ALL = Set.of(StatusType.GOOD, StatusType.BAD);

    private final String name;
    private final String description;
    private final StatusType type;
    private final StatusFlow flow;
    private final Statistic impactedProperty;
    private final StatusAmountFunction amountFunction;

    @Builder.Default
    private final StatusAmountTypeFunction typeFunction = (type) -> type; // identity if not changed by builder
    @Builder.Default
    private final boolean singleUse = false;
    @Builder.Default
    private final boolean equip = false;

    @Builder.Default
    private Integer lastsForTurns = null; // boxing allows null value

    public void aTurnIsPassed() {
        if (lastsForTurns != null)
            this.lastsForTurns--;
    }

    public void expended() {
        this.lastsForTurns = 0; // character clears this out at the end of turn
    }

    public static Set<StatusType> invertViewPoint(Set<StatusType> s) {
        if (ALL.containsAll(s))
            return ALL;
        else {
            Set<StatusType> inverted = new HashSet<>(ALL);
            inverted.removeAll(s);
            return inverted;
        }
    }
}
