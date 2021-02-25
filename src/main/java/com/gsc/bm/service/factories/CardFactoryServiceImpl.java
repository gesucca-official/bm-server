package com.gsc.bm.service.factories;

import com.gsc.bm.model.cards.Card;
import com.gsc.bm.model.cards.LoadableCard;
import com.gsc.bm.repo.internal.CardKeywordsRepository;
import com.gsc.bm.repo.internal.CardsGuiRecord;
import com.gsc.bm.repo.internal.CardsGuiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class CardFactoryServiceImpl implements CardFactoryService {

    private final CardsGuiRepository cardsGuiRepo;
    private final CardKeywordsRepository keywordsRepo;

    @Autowired
    public CardFactoryServiceImpl(CardsGuiRepository cardsGuiRepo, CardKeywordsRepository keywordsRepo) {
        this.cardsGuiRepo = cardsGuiRepo;
        this.keywordsRepo = keywordsRepo;
    }

    @Override
    public Card craftCard(String cardClazz) {
        Supplier<LoadableCard> supplier = () -> {
            try {
                return (LoadableCard) Class.forName(CardFactoryService.BASE_CARDS_PKG + cardClazz).getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return null; // this should really be managed better
            }
        };
        return craftCardFromConstructor(supplier);
    }

    private Card craftCardFromConstructor(Supplier<LoadableCard> cardConstructor) {
        LoadableCard card = cardConstructor.get();
        this.loadCard(card);
        return card;
    }

    private void loadCard(LoadableCard card) {
        String shortClassName = card.getClass().getName().replace(BASE_CARDS_PKG, "");
        CardsGuiRecord rec = cardsGuiRepo.findById(shortClassName)
                .orElseThrow(() -> new IllegalArgumentException("No such card in DB: " + shortClassName));
        card.setGuiName(rec.getGuiName());
        card.setGuiEffectDescription(buildHtml(card, rec.getGuiDescription()));
        card.setGuiImage(rec.getGuiImage());
        card.setGuiSprite(rec.getGuiSprite());
    }

    private String buildHtml(Card c, String d) {
        StringBuilder description = new StringBuilder();
        if (c.isBasicAction())
            appendHtml(description, "basicAction");
        if (c.isCharacterBound())
            appendHtml(description, "fallbackMove");
        if (c.getPriority() > 1)
            appendHtml(description, "firstStrike");
        if (c.getPriority() < 1)
            appendHtml(description, "lastStrike");

        return description.append("<p>").append(d).append("</p>").toString();
    }

    private void appendHtml(StringBuilder description, String fallbackMove) {
        description
                .append("<p>")
                .append(keywordsRepo.findById(fallbackMove)
                        .orElseThrow(() -> new IllegalArgumentException("No such Keyword in DB: " + fallbackMove))
                        .getHtml())
                .append("</p>");
    }
}
