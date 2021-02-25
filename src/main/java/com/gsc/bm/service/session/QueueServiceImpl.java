package com.gsc.bm.service.session;

import com.gsc.bm.service.session.model.QueuedPlayer;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Log4j2
public class QueueServiceImpl implements QueueService {

    private static final Map<GameQueue, Queue<QueuedPlayer>> QUEUES = new ConcurrentHashMap<>();
    private static final Map<GameQueue, Integer> MAX_QUEUE_SIZES = new HashMap<>(2);

    private final ConnectionsService connectionsService;

    @Autowired
    public QueueServiceImpl(ConnectionsService connectionsService) {
        this.connectionsService = connectionsService;

        // init queues
        QUEUES.put(GameQueue.Q_1V1, new ConcurrentLinkedQueue<>());
        MAX_QUEUE_SIZES.put(GameQueue.Q_1V1, 2);
        QUEUES.put(GameQueue.Q_FFA, new ConcurrentLinkedQueue<>());
        MAX_QUEUE_SIZES.put(GameQueue.Q_FFA, 5);
    }

    @Override
    public Optional<List<QueuedPlayer>> joinQueue(QueuedPlayer player, GameQueue queue) {
        log.info(player.getPlayerId() + " is joining queue: " + queue);
        QUEUES.get(queue).add(player);
        if (player.isHuman())
            connectionsService.userActivityChanged(player.getPlayerId(), "Queued for " + queue);

        if (QUEUES.get(queue).size() >= MAX_QUEUE_SIZES.get(queue)) {
            List<QueuedPlayer> playersInGame = new ArrayList<>(QUEUES.get(queue));
            QUEUES.get(queue).clear();
            log.info(queue + " queue is full, flushing players to Game");
            log.info(playersInGame);
            return Optional.of(playersInGame);
        }
        return Optional.empty();
    }

    @Override
    public Optional<List<QueuedPlayer>> flushQueue(GameQueue queue) {
        if (QUEUES.get(queue).size() >= 2) // minimum for any queue is 2 players, hardwired
        {
            List<QueuedPlayer> playersInGame = new ArrayList<>(QUEUES.get(queue));
            QUEUES.get(queue).clear();
            log.info(queue + " queue is flushing players to Game by user request");
            log.info(playersInGame);
            return Optional.of(playersInGame);
        }
        return Optional.empty();
    }

    @Override
    public void leaveQueue(QueuedPlayer player, GameQueue queue) {
        log.info(player.getPlayerId() + " leaves the " + queue + " queue");
        QUEUES.get(queue).remove(player);
        cleanQueuesFromCom();
    }

    @Override
    public void leaveAllQueues(QueuedPlayer player) {
        for (Map.Entry<GameQueue, Queue<QueuedPlayer>> q : QUEUES.entrySet())
            if (q.getValue().contains(player)) {
                q.getValue().remove(player);
                log.info(player.getPlayerId() + " leaves the " + q.getKey() + " queue");
            }
        cleanQueuesFromCom();
    }

    @Override
    public List<QueuedPlayer> getUsersInQueue(GameQueue queue) {
        return new ArrayList<>(QUEUES.get(queue));
    }

    private void cleanQueuesFromCom() {
        for (Queue<QueuedPlayer> q : QUEUES.values()) {
            if (q.stream().noneMatch(QueuedPlayer::isHuman))
                q.clear();
        }
    }
}
