package com.kandclay.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.kandclay.drop.Constants.*;

import java.util.Iterator;

public class DropGame extends Game {

    /*// Atlas
    TextureAtlas atlas;

    // Bucket
    private Rectangle bucket;
    private TextureRegion bucketTexture;

    // Raindrops
    private Array<Raindrop> raindrops;
    private Array<TextureRegion> dropletTextures;
    private Sound dropletSound;
    Array<MissedRaindrop> missedRaindrops = new Array<>();
    private long lastDropTime;
    private TextureRegion greyDropTexture;

    // Background
    private TextureRegion tileTexture;
    private int tileWidth;
    private int tileHeight;
    private int backgroundY = 0;


    // Extra
    private Music rainMusic;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Vector3 touchPos;

    // Controls
    private TextureRegion arrowLeftTexture;
    private TextureRegion arrowRightTexture;
    private Image arrowLeftButton;
    private Image arrowRightButton;
    Rectangle leftButtonBounds;
    Rectangle rightButtonBounds;
    boolean isLeftPressed = false;
    boolean isRightPressed = false;
    float bucketMoveAmount;

    // Stage
    Stage stage;
    Viewport viewport;*/

    @Override
    public void create() {

        // Create an instance of your GameScreen
        GameScreen gameScreen = new GameScreen(this); // Assuming GameScreen has a constructor that takes a Game instance

        // Set the screen to your game screen
        this.setScreen(gameScreen);

        /*dropletTextures = new Array<>();
        raindrops = new Array<>();
        touchPos = new Vector3();

        loadTextures();

        // Create raindrops
        for (int i = 0; i < 13; i++) {
            Rectangle rectangle = new Rectangle();
            Raindrop raindrop = new Raindrop(rectangle, dropletTextures.get(i));
            raindrops.add(raindrop);
        }

        createButtons();

        createViewport();

        // Sounds
        dropletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/rain.mp3"));

        rainMusic.setLooping(true);
        rainMusic.play();

        // Objects
        bucket = new Rectangle();
        bucket.x = (float) HEIGHT / 2 - (float) BUCKET_WIDTH / 2;
        bucket.y = BUCKET_INITIAL_Y;
        bucket.width = BUCKET_WIDTH;
        bucket.height = BUCKET_HEIGHT;

        // Background parameters
        tileHeight = (int) (tileTexture.getRegionHeight() * SCALE_FACTOR);
        tileWidth = (int) (tileTexture.getRegionWidth() * SCALE_FACTOR);*/
    }

    @Override
    public void render() {
        super.render();

        /*ScreenUtils.clear(0, 0, 0.2f, 1); // Light blue

        camera.update();

        // Draw the assets according to their current position
        batch.begin();
        renderBackground(batch);
        batch.draw(bucketTexture, bucket.x, bucket.y);
        for (Raindrop raindrop : raindrops) {
            float x = raindrop.getRectangle().x;
            float y = raindrop.getRectangle().y;
            batch.draw(raindrop.getTexture(), x, y);
        }
        batch.end();

        updateRaindropPosition();
        renderMissedRaindrops();

        float deltaTime = Gdx.graphics.getDeltaTime();
        bucketMoveAmount = BUCKET_MOVE_SPEED * deltaTime;

        backgroundY -= (int) (BACKGROUND_SCROLL_SPEED * deltaTime);
        if (backgroundY < -tileTexture.getRegionHeight() * BACKGROUND_SCROLL_SPEED) {
            backgroundY = 0;
        }

        updateBlinkingEffect(deltaTime);
        handleControls();

        if (TimeUtils.nanoTime() - lastDropTime > ONE_SECOND_NS) {
            spawnRaindrop();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();*/
    }

    @Override
    public void resize(int width, int height) {
        /*viewport.update(width, height, true);
        // Update the camera with the new window size
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        // Update the stage's viewport as well, if you are using Scene2D UI
        stage.getViewport().update(width, height, true);

        // Call a method to update the button bounds
        updateButtonBounds();*/
    }

    private void updateButtonBounds() {
        /*leftButtonBounds.set(arrowLeftButton.getX(), arrowLeftButton.getY(),
                arrowLeftButton.getWidth(), arrowLeftButton.getHeight());
        rightButtonBounds.set(arrowRightButton.getX(), arrowRightButton.getY(),
                arrowRightButton.getWidth(), arrowRightButton.getHeight());*/
    }

    @Override
    public void dispose() {
        /*atlas.dispose();
        stage.dispose();
        dropletSound.dispose();
        rainMusic.dispose();
        batch.dispose();*/
    }
}
