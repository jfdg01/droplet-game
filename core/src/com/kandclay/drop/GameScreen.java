package com.kandclay.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

import static com.kandclay.drop.Constants.*;
import static com.kandclay.drop.Constants.SCALE_FACTOR;

public class GameScreen implements Screen {

    private DropGame game;

    // Atlas
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
    Viewport viewport;

    public GameScreen(DropGame game) {
        this.game = game;

        createGame();
    }

    public void createGame() {
        dropletTextures = new Array<>();
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
        tileWidth = (int) (tileTexture.getRegionWidth() * SCALE_FACTOR);
    }

    @Override
    public void show() {

    }

    private void renderLogic(float delta) {
        // Draw the assets according to their current position
        batch.begin();
        batch.setProjectionMatrix(camera.combined);

        renderBackground(batch);
        for (Raindrop raindrop : raindrops) {
            float x = raindrop.getRectangle().x;
            float y = raindrop.getRectangle().y;
            batch.draw(raindrop.getTexture(), x, y);
        }
        batch.draw(bucketTexture, bucket.x, bucket.y);

        batch.end();

        renderMissedRaindrops();
    }

    private void updateLogic(float delta) {
        bucketMoveAmount = BUCKET_MOVE_SPEED * delta;

        backgroundY -= (int) (BACKGROUND_SCROLL_SPEED * delta);
        if (backgroundY < -tileTexture.getRegionHeight() * BACKGROUND_SCROLL_SPEED) {
            backgroundY = 0;
        }

        updateRaindropPosition();

        updateBlinkingEffect(delta);

        spawnRaindrop();

        camera.update();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1); // Light blue

        updateLogic(delta);

        renderLogic(delta);

        handleControls();

        stage.act(delta);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Only update the viewport, it will adjust the camera as needed
        viewport.update(width, height);

        // Update the stage's viewport as well, if you are using Scene2D UI
        stage.getViewport().update(width, height, true);

        // Call a method to update the button bounds
        updateButtonBounds();
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        atlas.dispose();
        stage.dispose();
        dropletSound.dispose();
        rainMusic.dispose();
        batch.dispose();
    }

    public void handleArrowKeys(float moveAmount) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (bucket.x - moveAmount < LEFT_OF_SCREEN) {
                bucket.x = 0;
            } else {
                bucket.x -= moveAmount;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (bucket.x + moveAmount > RIGHT_OF_SCREEN - BUCKET_WIDTH) {
                bucket.x = RIGHT_OF_SCREEN - BUCKET_WIDTH;
            } else {
                bucket.x += moveAmount;
            }
        }

    }

    private void handleMovementButtons(float bucketMoveAmount) {
        if (isLeftPressed) {
            if (bucket.x - bucketMoveAmount < LEFT_OF_SCREEN) {
                bucket.x = 0;
            } else {
                bucket.x -= bucketMoveAmount;
            }
        }

        if (isRightPressed) {
            if (bucket.x + bucketMoveAmount > RIGHT_OF_SCREEN - BUCKET_WIDTH) {
                bucket.x = RIGHT_OF_SCREEN - BUCKET_WIDTH;
            } else {
                bucket.x += bucketMoveAmount;
            }
        }
    }

    private void handleControls() {
        handleMovementButtons(bucketMoveAmount);
        handleArrowKeys(bucketMoveAmount);
        handleTouchInput();
    }

    public void loadTextures() {

        atlas = new TextureAtlas(Gdx.files.internal("atlas/main.atlas"));
        bucketTexture = atlas.findRegion("bucket");
        greyDropTexture = atlas.findRegion("droplet-greyscale");
        tileTexture = atlas.findRegion("rain");
        arrowLeftTexture = atlas.findRegion("arrow-left");
        arrowRightTexture = atlas.findRegion("arrow-right");

        dropletTextures = new Array<>();
        for (int i = 0; i < 13; i++) {
            TextureRegion dropletTexture = atlas.findRegion("droplet-" + i);
            if (dropletTexture != null) {
                dropletTextures.add(dropletTexture);
            }
        }
    }

    private void createButtons() {
        arrowLeftButton = new Image(new TextureRegionDrawable(new TextureRegion(arrowLeftTexture)));
        arrowRightButton = new Image(new TextureRegionDrawable(new TextureRegion(arrowRightTexture)));

        // Set the size of the buttons
        float buttonSize = BUCKET_WIDTH; // Same size as the bucket
        arrowLeftButton.setSize(buttonSize, buttonSize);
        arrowRightButton.setSize(buttonSize, buttonSize);

        // Position the buttons at the top center of the screen
        float buttonY = HEIGHT - buttonSize - PADDING_TOP_PIXELS; // 20 pixels from the top
        float centerX = (float) WIDTH / 2;
        arrowLeftButton.setPosition(centerX - buttonSize - PADDING_BETWEEN_BUTTONS, buttonY); // 10 pixels apart
        arrowRightButton.setPosition(centerX + PADDING_BETWEEN_BUTTONS, buttonY);

        leftButtonBounds = new Rectangle(arrowLeftButton.getX(), arrowLeftButton.getY(),
                arrowLeftButton.getWidth(), arrowLeftButton.getHeight());
        rightButtonBounds = new Rectangle(arrowRightButton.getX(), arrowRightButton.getY(),
                arrowRightButton.getWidth(), arrowRightButton.getHeight());
        arrowLeftButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isLeftPressed = true;
                return true; // Return true to indicate the event was handled
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isLeftPressed = false;
            }
        });

        arrowRightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isRightPressed = true;
                return true; // Return true to indicate the event was handled
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isRightPressed = false;
            }
        });
    }

    private void updateButtonBounds() {
        leftButtonBounds.set(arrowLeftButton.getX(), arrowLeftButton.getY(),
                arrowLeftButton.getWidth(), arrowLeftButton.getHeight());
        rightButtonBounds.set(arrowRightButton.getX(), arrowRightButton.getY(),
                arrowRightButton.getWidth(), arrowRightButton.getHeight());
    }

    private void createViewport() {
        // Batch
        batch = new SpriteBatch();

        // Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        // Stage
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage); // Set the stage as the input processor
        stage.addActor(arrowLeftButton);
        stage.addActor(arrowRightButton);
    }

    private void updateRaindropPosition() {
        for (Iterator<Raindrop> iter = raindrops.iterator(); iter.hasNext(); ) {

            Raindrop raindrop = iter.next();
            raindrop.getRectangle().y -= RAINDROP_FALL_SPEED * Gdx.graphics.getDeltaTime();

            // Remove raindrop if it's below the screen
            if (raindrop.getRectangle().y + RAINDROP_WIDTH < 0) {
                missedRaindrops.add(new MissedRaindrop(raindrop.getRectangle().x));
                iter.remove();
            }

            // Remove raindrop if it's in the bucket
            if (raindrop.getRectangle().overlaps(bucket)) {
                dropletSound.play();
                iter.remove();
            }
        }
    }

    private void renderMissedRaindrops() {
        batch.begin();
        batch.setColor(1, 1, 1, 0.7f); // Set opacity to 70%
        for (MissedRaindrop missed : missedRaindrops) {
            if (missed.visible) {
                batch.draw(greyDropTexture, missed.x, BOTTOM_OF_SCREEN);
            }
            if (missed.dead) {
                missedRaindrops.removeValue(missed, true);
            }
        }
        batch.setColor(1, 1, 1, 1); // Reset opacity to 100%
        batch.end();
    }

    private void updateBlinkingEffect(float deltaTime) {
        for (MissedRaindrop missed : missedRaindrops) {
            missed.timer += deltaTime;
            if (missed.timer >= BLINKING_TIME) {
                missed.visible = !missed.visible;
                missed.timer = 0;
                if (!missed.visible) {
                    missed.times_blinked++;
                }
            }

            if (missed.times_blinked >= MAX_TIMES_BLINKED) {
                missed.dead = true;
            }
        }
    }

    private void handleTouchInput() {
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            // Check if the touch is within the bounds of either button
            if (!leftButtonBounds.contains(touchPos.x, touchPos.y) &&
                    !rightButtonBounds.contains(touchPos.x, touchPos.y)) {
                bucket.x = touchPos.x - (float) BUCKET_WIDTH / 2;

                // Ensure bucket stays within screen bounds
                if (bucket.x < LEFT_OF_SCREEN) {
                    bucket.x = LEFT_OF_SCREEN;
                } else if (bucket.x > RIGHT_OF_SCREEN - BUCKET_WIDTH) {
                    bucket.x = RIGHT_OF_SCREEN - BUCKET_WIDTH;
                }
            }
        }
    }

    private void spawnRaindrop() {
        if (TimeUtils.nanoTime() - lastDropTime > ONE_SECOND_NS) {
            Rectangle rectangle = new Rectangle();
            rectangle.x = MathUtils.random(0, WIDTH - BUCKET_WIDTH);
            rectangle.y = HEIGHT;
            rectangle.width = RAINDROP_WIDTH;
            rectangle.height = RAINDROP_HEIGHT;

            int randomInt = MathUtils.random(0, 12);
            TextureRegion textureRegion = atlas.findRegion("droplet-" + randomInt);

            Raindrop raindrop = new Raindrop(rectangle, textureRegion);

            raindrops.add(raindrop);
            lastDropTime = TimeUtils.nanoTime();
        }
    }

    private void renderBackground(SpriteBatch batch) {
        float scaledHeight = tileTexture.getRegionHeight() * SCALE_FACTOR;
        float scaledWidth = tileTexture.getRegionWidth() * SCALE_FACTOR;

        for (float x = LEFT_OF_SCREEN; x < WIDTH; x += scaledWidth) {
            for (float y = backgroundY; y < HEIGHT; y += scaledHeight) {
                batch.draw(tileTexture, x, y, scaledWidth, scaledHeight);
            }
        }
    }
}
