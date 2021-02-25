package com.gsc.bm.model.game;

import com.gsc.bm.model.Character;
import lombok.Getter;

import java.io.Serializable;
import java.util.function.Consumer;

@Getter
public class Timer implements Serializable {

    private final String name;
    private final TimerCharacterConsumer callback;
    private int timer;

    public Timer(String name, TimerCharacterConsumer callback, int timer) {
        this.name = name;
        this.callback = callback;
        this.timer = timer;
    }

    public void timeTick() {
        timer--;
    }

    public interface TimerCharacterConsumer extends Consumer<Character>, Serializable {
        // this is just so it can extends serializable
    }

}
