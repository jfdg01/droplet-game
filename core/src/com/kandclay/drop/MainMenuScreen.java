package com.kandclay.drop;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.kandclay.drop.Constants.*;

public class MainMenuScreen implements Screen {

    private final Skin skin;
    private TextButton playButton;
    private TextButton exitButton;
    private final Game game;
    private Stage stage;

    public MainMenuScreen(DropGame game) {
        this.game = game;

        skin = new Skin(Gdx.files.internal("skins/pixthulhuui/pixthulhu-ui.json"), new TextureAtlas(Gdx.files.internal("skins/pixthulhuui/pixthulhu-ui.atlas")));

        // Initialize stage and set it as input processor
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Create a play button
        playButton = new TextButton("Play", skin);
        playButton.setPosition(PLAY_BUTTON_X, PLAY_BUTTON_Y);
        playButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game)); // Change to your game screen
            }
        });
        stage.addActor(playButton);

        // Similarly, create an exit button
        exitButton = new TextButton("Exit", skin);
        exitButton.setPosition(EXIT_BUTTON_X, EXIT_BUTTON_Y);
        exitButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        stage.addActor(exitButton);
    }

    @Override
    public void show() {
        // This method will be called when the MainMenuScreen is set as the current screen.
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the stage and its actors (buttons)
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update your screen layout here if necessary
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // This method will be called when the app is paused
    }

    @Override
    public void resume() {
        // This method will be called when the app is resumed from a paused state
    }

    @Override
    public void hide() {
        // This method will be called when the MainMenuScreen is no longer the current screen
    }

    @Override
    public void dispose() {
        // Dispose of assets and resources
        stage.dispose();
        skin.dispose();
    }
}
