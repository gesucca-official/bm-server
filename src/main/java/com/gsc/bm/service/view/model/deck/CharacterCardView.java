package com.gsc.bm.service.view.model.deck;

import com.gsc.bm.model.Resource;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Builder
@Getter
public class CharacterCardView {
    private final String name;
    private final String bindingName;
    private final String sprite;
    private final int itemsSize;
    private final Map<Resource, Integer> resources;
    private final Set<Resource> immunities;
}
