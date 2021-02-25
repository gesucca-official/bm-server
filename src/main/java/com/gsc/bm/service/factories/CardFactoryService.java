package com.gsc.bm.service.factories;

import com.gsc.bm.model.cards.Card;

public interface CardFactoryService {

    String BASE_CARDS_PKG = "com.gsc.bm.model.cards.";

    Card craftCard(String cardClazz);

}
