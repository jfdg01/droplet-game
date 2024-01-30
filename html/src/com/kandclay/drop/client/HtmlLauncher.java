package com.kandclay.drop.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.kandclay.drop.DropGame;

public class HtmlLauncher extends GwtApplication {

    @Override
    public GwtApplicationConfiguration getConfig() {
        // Resizable application, uses available space in browser
        return new GwtApplicationConfiguration(800,480);
        // Fixed size application:
        //return new GwtApplicationConfiguration(480, 320);
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new DropGame();
    }
}