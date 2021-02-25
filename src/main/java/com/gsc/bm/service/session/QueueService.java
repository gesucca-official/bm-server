package com.gsc.bm.service.session;

import com.gsc.bm.service.session.model.QueuedPlayer;

import java.util.List;
import java.util.Optional;

public interface QueueService {

    enum GameQueue {
        Q_1V1, Q_FFA
    }

    Optional<List<QueuedPlayer>> joinQueue(QueuedPlayer player, GameQueue queue);

    Optional<List<QueuedPlayer>> flushQueue(GameQueue queue);

    void leaveQueue(QueuedPlayer player, GameQueue queue);

    void leaveAllQueues(QueuedPlayer player);

    List<QueuedPlayer> getUsersInQueue(GameQueue queue);
}
