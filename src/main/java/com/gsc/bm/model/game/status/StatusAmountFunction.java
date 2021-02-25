package com.gsc.bm.model.game.status;

import java.io.Serializable;
import java.util.function.Function;

public interface StatusAmountFunction extends Function<Float, Float>, Serializable {
    // this is just so it can extends serializable
}
