package com.gsc.bm.model.game;

import lombok.Getter;

@Getter
public class IllegalMoveException extends RuntimeException {
    private final String playerId, whatHeDid;

    public IllegalMoveException(String playerId, String whatHeDid) {
        super();
        this.playerId = playerId;
        this.whatHeDid = whatHeDid;
    }
}
