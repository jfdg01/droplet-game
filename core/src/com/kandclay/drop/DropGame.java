package com.kandclay.drop;

import com.badlogic.gdx.Game;

public class DropGame extends Game {

    GameScreen gameScreen;
    MainMenuScreen mainMenuScreen;

    @Override
    public void create() {

        mainMenuScreen = new MainMenuScreen(this);

        this.setScreen(mainMenuScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void dispose() {

    }

    public void startGame() {
        gameScreen = new GameScreen(this);
        this.setScreen(gameScreen);
    }
}
