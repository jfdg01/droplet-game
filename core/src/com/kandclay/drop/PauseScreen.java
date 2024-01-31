package com.kandclay.drop;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import static com.kandclay.drop.Constants.*;

public class PauseScreen implements Screen {

    private final DropGame game;
    SpriteBatch batch;
    private Stage stage;
    private Skin skin;
    private TextButton resumeButton;
    private TextButton exitButton;
    private Texture backgroundTexture;

    private GameScreen gameScreen;

    public PauseScreen(final DropGame game, Texture backgroundTexture, SpriteBatch batch, GameScreen gameScreen) {
        this.game = game;
        this.backgroundTexture = backgroundTexture;
        this.batch = batch;
        this.gameScreen = gameScreen;

        // Initialize the stage and skin
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("skins/pixthulhuui/pixthulhu-ui.json"), new TextureAtlas(Gdx.files.internal("skins/pixthulhuui/pixthulhu-ui.atlas")));

        // Create and position the resume button
        resumeButton = new TextButton("Resume", skin);
        resumeButton.setPosition(PLAY_BUTTON_X, PLAY_BUTTON_Y); // Adjust position as needed
        resumeButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(gameScreen); // Resume the existing GameScreen
            }
        });
        stage.addActor(resumeButton);

        // Create and position the exit button
        exitButton = new TextButton("Exit to Menu", skin);
        exitButton.setPosition(EXIT_BUTTON_X, EXIT_BUTTON_Y); // Adjust position as needed
        exitButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game, batch)); // Exit to main menu
            }
        });
        stage.addActor(exitButton);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the blurred background
        game.getBatch().begin();
        game.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.getBatch().end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}

