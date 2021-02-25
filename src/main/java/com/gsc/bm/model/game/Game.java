package com.gsc.bm.model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gsc.bm.model.Resource;
import com.gsc.bm.model.Character;
import com.gsc.bm.model.cards.Card;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Hex;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Log4j2
@Getter
@ToString
public class Game implements Serializable {

    // TODO move this in its own file?
    public static class LoggingList extends ArrayList<String> implements Serializable {

        private final boolean printLogs;

        public LoggingList(boolean printLogs) {
            this.printLogs = printLogs;
        }

        @Override
        public boolean add(String s) {
            if (printLogs)
                log.info(s);
            return super.add(s);
        }
    }

    private String gameId;
    private final Map<String, Player> players;

    private final List<Move> pendingMoves = new ArrayList<>();

    private final List<Move> resolvedMoves = new ArrayList<>();
    private final Map<String, List<String>> timeBasedEffects = new HashMap<>();

    private int turn = 0; // why keeping it here? client could use it
    @JsonIgnore
    private final String type;
    @JsonIgnore
    private final LoggingList loggedTurnEvents;

    @JsonProperty
    public boolean isOver() {
        int dead = 0;
        for (String playerId : players.keySet())
            if (players.get(playerId).getCharacter().isDead())
                dead++;
        return dead >= players.size() - 1;
    }

    @JsonProperty
    public Optional<String> getWinner() {
        if (isOver())
            for (String playerId : players.keySet())
                if (!players.get(playerId).getCharacter().isDead())
                    return Optional.of(players.get(playerId).getPlayerId());
        return Optional.empty();
    }

    public Game(List<Player> players, String type, boolean printLogs) {
        generateGameId(players);
        this.type = type;
        // rearrange players in a map
        this.players = new HashMap<>(players.size());
        for (Player p : players)
            this.players.put(p.getPlayerId(), p);

        loggedTurnEvents = new LoggingList(printLogs);
    }

    public void submitMove(Move move) throws IllegalMoveException {
        Move.MoveCheckResult moveCheckResult = move.isValidFor(this);
        if (moveCheckResult.isValid())
            pendingMoves.add(move);
        else {
            loggedTurnEvents.add("An Invalid Move has been Submitted: " + move);
            throw new IllegalMoveException(move.getPlayerId(), moveCheckResult.getComment());
        }
    }

    @JsonIgnore
    public boolean isReadyToResolveMoves() {
        autoSubmitDeadPlayersMove();
        autoSubmitComPlayersMove();
        return !isOver() && pendingMoves.size() == players.size();
    }

    public void resolveMoves(Consumer<Game> endChainCallback, BiConsumer<Integer, List<String>> turnEventsLogDrain) {
        if (!isReadyToResolveMoves())
            throw new RuntimeException("Trying to Resolve Moves when Game is not Ready!");

        pendingMoves.removeAll(
                pendingMoves.stream().filter(Move::isVoid).collect(Collectors.toList())
        );

        for (Move m : pendingMoves)
            m.applyCostTo(this);

        pendingMoves.sort(
                Comparator.comparingInt(
                        m -> {
                            Move move = (Move) m; // I am not sure why I have to downcast here
                            return -getCardFromHand(move.getPlayerId(), move.getPlayedCardName()).getPriority();
                        }
                ).thenComparingInt(
                        m -> -getSelf((Move) m).getCharacter().getResources().get(Resource.ALERTNESS)
                ));

        for (Move m : pendingMoves) {
            m.applyEffectTo(this);
            getSelf(m).discardCard(getCardFromHand(m.getPlayerId(), m.getPlayedCardName()));
            if (getSelf(m).getCardsInHand().size() < Player.CARDS_IN_HAND)
                getSelf(m).drawCard();
        }

        resolvedMoves.clear();
        resolvedMoves.addAll(pendingMoves);
        pendingMoves.clear();

        timeBasedEffects.clear();
        for (String playerId : players.keySet())
            if (!players.get(playerId).getCharacter().isDead())
                timeBasedEffects.put(playerId, players.get(playerId).getCharacter().resolveTimeBasedEffects());

        loggedTurnEvents.add("Turn " + turn + " has been Resolved.");
        turnEventsLogDrain.accept(turn, loggedTurnEvents);
        turn++;

        if (!isOver() && isReadyToResolveMoves())
            resolveMoves(endChainCallback, turnEventsLogDrain);
        else endChainCallback.accept(this);
    }

    @JsonIgnore
    public List<Player> getOpponents(String playerId) {
        List<Player> opponents = new ArrayList<>(players.size());
        players.values().forEach(p -> {
            if (!p.getPlayerId().equals(playerId) && !p.getCharacter().isDead())
                opponents.add(p);
        });
        return opponents;
    }

    @JsonIgnore
    public Card getCardFromHand(String playerId, String cardName) {
        // getting this card also checks if it is in that player's hand adn throws exception accordingly
        return players.get(playerId).getCardsInHand().stream()
                .filter(c -> c.getName().equalsIgnoreCase(cardName))
                .findAny()
                .orElseThrow(() -> {
                    loggedTurnEvents.add("Player " + playerId + " is trying to get an Illegal Card from Hand: " + cardName);
                    return new IllegalMoveException(playerId, "don't have that card in hand");
                });
    }

    @JsonIgnore
    public Card getItem(String playerId, String itemCardName) {
        if (!players.containsKey(playerId))
            throw new IllegalMoveException(playerId, "there's no such player");
        for (Card item : players.get(playerId).getCharacter().getItems())
            if (item.getName().equals(itemCardName))
                return item;
        loggedTurnEvents.add("Someone is trying to get an Illegal Item Card from " + playerId + " Items: " + itemCardName);
        throw new IllegalMoveException(playerId, "don't have that item");
    }

    @JsonIgnore
    public Player getSelf(Move move) {
        return players.get(move.getPlayerId());
    }

    @JsonIgnore
    public Player getTarget(Move move) {
        if (move.getTargetId().equalsIgnoreCase("SELF"))
            return players.get(move.getPlayerId());
        else
            return players.get(move.getTargetId());
    }

    @JsonIgnore
    public Optional<Move> getPendingMoveOfTargetIfMovesAfterPlayer(String playerId, String targetId) {
        loggedTurnEvents.add("Player " + playerId + "'s Move has queried for the Successive Move of " + targetId);
        // this is called by cards when they resolves, so pending moves are already sorted in resolution order
        for (int i = 0; i < pendingMoves.size(); i++) {
            if (pendingMoves.get(i).getPlayerId().equals(playerId))
                for (int j = i + 1; j < pendingMoves.size(); j++)
                    if (pendingMoves.get(j).getPlayerId().equals(targetId)) {
                        loggedTurnEvents.add("Successive Move found: " + pendingMoves.get(j));
                        return Optional.of(pendingMoves.get(j));
                    }
            // those nested cycles, those indexes... god that smells bad
        }
        loggedTurnEvents.add("Found nothing! This should not have happened.");
        loggedTurnEvents.add(pendingMoves.toString());
        return Optional.empty();
    }

    private void generateGameId(List<Player> players) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            final byte[] hashBytes = digest.digest(
                    (players.toString() + System.currentTimeMillis())
                            .getBytes(StandardCharsets.UTF_8));
            this.gameId = Hex.encodeHexString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            this.gameId = players.toString() + System.currentTimeMillis();
        }
    }

    private void autoSubmitComPlayersMove() {
        // is a player is an AI player, automatically submit its move
        for (Player p : getPlayers().values())
            if (p instanceof ComPlayer && pendingMoves.stream().noneMatch(m -> m.getPlayerId().equals(p.getPlayerId()))) {
                Move comMove = ((ComPlayer) p).chooseMove(this);
                loggedTurnEvents.add("Player " + p.getPlayerId() + " auto submit Move: " + comMove);
                submitMove(comMove);
            }
    }

    private void autoSubmitDeadPlayersMove() {
        // dead players do nothing by default
        players.values().forEach(p -> {
            if (p.getCharacter().isDead() && pendingMoves.stream().noneMatch(m -> m.getPlayerId().equals(p.getPlayerId()))) {
                loggedTurnEvents.add("Player " + p.getPlayerId() + " is dead and is auto submitting an Empty Move");
                submitMove(Move.voidMove(p.getPlayerId()));
            }
        });
    }
}
