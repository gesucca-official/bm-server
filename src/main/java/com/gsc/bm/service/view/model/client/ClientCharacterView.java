package com.gsc.bm.service.view.model.client;

import com.gsc.bm.model.Resource;
import com.gsc.bm.model.cards.Card;
import com.gsc.bm.model.game.Timer;
import com.gsc.bm.model.game.status.Status;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@Builder
@Getter
public class ClientCharacterView {
    private final String name;
    private final int itemsSize;
    private final Queue<Card> items;
    private final Map<Resource, Integer> resources;
    private final List<Status> statuses;
    private final Set<Resource> immunities;
    private final Set<Timer> timers;

    private final String sprite;
}
