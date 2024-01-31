package com.kandclay.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DropGame extends Game {

    GameScreen gameScreen;
    MainMenuScreen mainMenuScreen;
    SpriteBatch batch;

    @Override
    public void create() {

        batch = new SpriteBatch();

        mainMenuScreen = new MainMenuScreen(this, batch);

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



        gameScreen = new GameScreen(this, batch);
        this.setScreen(gameScreen);
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
