package com.kandclay.drop;

public class MissedRaindrop {
    float x;
    float timer;
    boolean visible;
    boolean dead;
    int times_blinked;

    public MissedRaindrop(float x) {
        this.x = x;
        this.timer = 0;
        this.visible = true;
    }

}
