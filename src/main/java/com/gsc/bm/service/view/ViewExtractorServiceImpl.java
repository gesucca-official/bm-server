package com.gsc.bm.service.view;

import com.gsc.bm.model.Character;
import com.gsc.bm.model.cards.Card;
import com.gsc.bm.model.game.Game;
import com.gsc.bm.model.game.Move;
import com.gsc.bm.model.game.Player;
import com.gsc.bm.model.game.status.Status;
import com.gsc.bm.repo.internal.CharactersGuiRecord;
import com.gsc.bm.repo.internal.CharactersGuiRepository;
import com.gsc.bm.service.view.model.client.*;
import com.gsc.bm.service.view.model.deck.CharacterCardView;
import com.gsc.bm.service.view.model.logging.SlimCharacterView;
import com.gsc.bm.service.view.model.logging.SlimGameView;
import com.gsc.bm.service.view.model.logging.SlimPlayerView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.gsc.bm.service.factories.CardFactoryService.BASE_CARDS_PKG;

@Service
public class ViewExtractorServiceImpl implements ViewExtractorService {

    private final CharactersGuiRepository charactersRepo;

    @Autowired
    public ViewExtractorServiceImpl(CharactersGuiRepository charactersRepo) {
        this.charactersRepo = charactersRepo;
    }

    @Override
    public SlimGameView extractGlobalSlimView(Game game) {
        byte[] bytes = SerializationUtils.serialize(game);
        Game gameClone = (Game) SerializationUtils.deserialize(bytes);

        assert gameClone != null;
        List<SlimPlayerView> slimPlayers = gameClone.getPlayers().values()
                .stream()
                .map(p -> SlimPlayerView.builder()
                        .playerId(p.getPlayerId())
                        .character(toSlimView(p.getCharacter()))
                        .cardsInHand(p.getCardsInHand()
                                .stream()
                                .map(Card::getName)
                                .collect(Collectors.toList())
                        ).deck(p.getDeck()
                                .stream()
                                .map(Card::getName)
                                .collect(Collectors.toList())
                        ).build())
                .collect(Collectors.toList());

        return SlimGameView.builder()
                .gameId(gameClone.getGameId())
                .players(slimPlayers)
                .resolvedMoves(gameClone.getResolvedMoves()
                        .stream()
                        .map(Move::getSlimView)
                        .collect(Collectors.toList())
                ).timeBasedEffects(gameClone.getTimeBasedEffects())
                .over(game.isOver())
                .winner(game.getWinner().orElse("NONE"))
                .build();
    }

    private SlimCharacterView toSlimView(Character character) {
        String clazz = character.getClass().getName().replace(BASE_CARDS_PKG, "");
        CharactersGuiRecord record = charactersRepo.findById(clazz)
                .orElseThrow(() -> new RuntimeException("Couldn't load Character from internal DB! " + clazz));
        return SlimCharacterView.builder()
                .name(record.getGuiName())
                .items(character.getItems().stream()
                        .map(Card::getName)
                        .collect(Collectors.toList()))
                .resources(character.getResources())
                .statuses(character.getStatuses().stream()
                        .map(Status::getName)
                        .collect(Collectors.toList())
                ).immunities(character.getImmunities())
                .build();
    }

    @Override
    public ClientGameView extractViewFor(Game game, String playerId) {
        byte[] bytes = SerializationUtils.serialize(game);
        Game gameClone = (Game) SerializationUtils.deserialize(bytes);
        assert gameClone != null;

        return ClientGameView.builder()
                .gameId(gameClone.getGameId())
                .players(gameClone.getPlayers().values()
                        .stream()
                        .map(p -> {
                            if (!p.getPlayerId().equals(playerId))
                                return toClientViewForOpponents(p);
                            else return toClientViewForSelf(p);
                        })
                        .collect(Collectors.toMap(AbstractClientPlayerView::getPlayerId, Function.identity()))
                )
                .resolvedMoves(gameClone.getResolvedMoves())
                .timeBasedEffects(gameClone.getTimeBasedEffects())
                .over(gameClone.isOver())
                .winner(gameClone.getWinner().orElse("NONE"))
                .build();
    }

    private ClientSelfPlayerView toClientViewForSelf(Player player) {
        byte[] bytes = SerializationUtils.serialize(player);
        Player p = (Player) SerializationUtils.deserialize(bytes);
        assert p != null;
        return ClientSelfPlayerView.builder()
                .playerId(p.getPlayerId())
                .character(toClientView(p.getCharacter()))
                .cardsInHand(p.getCardsInHand())
                .deck(p.getDeck().size())
                .build();
    }

    private ClientOpponentView toClientViewForOpponents(Player player) {
        byte[] bytes = SerializationUtils.serialize(player);
        Player p = (Player) SerializationUtils.deserialize(bytes);
        assert p != null;
        return ClientOpponentView.builder()
                .playerId(p.getPlayerId())
                .character(toClientView(p.getCharacter()))
                .cardsInHand(p.getCardsInHand().size())
                .deck(p.getDeck().size())
                .build();
    }


    // TODO duplicate code come on g

    private ClientCharacterView toClientView(Character character) {
        byte[] bytes = SerializationUtils.serialize(character);
        Character c = (Character) SerializationUtils.deserialize(bytes);
        assert c != null;

        String clazz = character.getClass().getName().replace(BASE_CARDS_PKG, "");
        CharactersGuiRecord record = charactersRepo.findById(clazz)
                .orElseThrow(() -> new RuntimeException("Couldn't load Character from internal DB! " + clazz));
        return ClientCharacterView.builder()
                .name(record.getGuiName())
                .itemsSize(c.getItemsSize())
                .items(c.getItems())
                .resources(c.getResources())
                .statuses(c.getStatuses())
                .immunities(c.getImmunities())
                .timers(c.getTimers())
                .sprite(record.getGuiImage()) // TODO load this at once in a character factory??
                .build();
    }

    @Override
    public CharacterCardView extractDeckBuildingView(Character character) {
        byte[] bytes = SerializationUtils.serialize(character);
        Character c = (Character) SerializationUtils.deserialize(bytes);
        assert c != null;

        String clazz = character.getClass().getName().replace(BASE_CARDS_PKG, "");
        CharactersGuiRecord record = charactersRepo.findById(clazz)
                .orElseThrow(() -> new RuntimeException("Couldn't load Character from internal DB! " + clazz));
        return CharacterCardView.builder()
                .name(record.getGuiName())
                .bindingName(character.getBindingName())
                .sprite(record.getGuiImage())
                .itemsSize(c.getItemsSize())
                .resources(c.getResources())
                .immunities(c.getImmunities())
                .build();
    }
}
