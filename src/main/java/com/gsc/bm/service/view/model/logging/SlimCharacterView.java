package com.gsc.bm.service.view.model.logging;

import com.gsc.bm.model.Resource;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Getter
public class SlimCharacterView implements Serializable {
    private final String name;
    private final List<String> items;
    private final Map<Resource, Integer> resources;
    private final List<String> statuses;
    private final Set<Resource> immunities;
}
