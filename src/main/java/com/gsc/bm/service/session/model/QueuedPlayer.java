package com.gsc.bm.service.session.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class QueuedPlayer {
    private final String playerId;
    private final boolean isHuman;
}
