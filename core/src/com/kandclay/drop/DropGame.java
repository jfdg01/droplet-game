package com.kandclay.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.kandclay.drop.Constants.*;

import java.util.Iterator;

public class DropGame extends ApplicationAdapter {

    // Bucket
    private Rectangle bucket;
    private Texture bucketTexture;

    // Raindrops
    private Array<Raindrop> raindrops;
    private Array<Texture> dropletTextures;
    private Sound dropletSound;
    Array<MissedRaindrop> missedRaindrops = new Array<>();
    private long lastDropTime;
    private Texture greyDropTexture;

    // Background
    private Texture tileTexture;
    private int tileWidth;
    private int tileHeight;
    private int backgroundY = 0;


    // Extra
    private Music rainMusic;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Vector3 touchPos;

    // Controls
    private Texture arrowLeftTexture;
    private Texture arrowRightTexture;
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

    public void loadTextures() {
        bucketTexture = new Texture(Gdx.files.internal("textures/bucket.png"));
        greyDropTexture = new Texture(Gdx.files.internal("textures/droplet-greyscale.png"));
        tileTexture = new Texture(Gdx.files.internal("textures/rain.jpg"));
        arrowLeftTexture = new Texture(Gdx.files.internal("textures/arrow-left.png"));
        arrowRightTexture = new Texture(Gdx.files.internal("textures/arrow-right.png"));
        for (int i = 0; i < 13; i++) {
            Texture texture = new Texture(Gdx.files.internal("textures/droplet-" + i + ".png"));
            dropletTextures.add(texture);
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

    private void createViewport() {
        // Batch
        batch = new SpriteBatch();

        // Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        // Stage
        viewport = new FitViewport(800, 480, camera); // Use your game's world size
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage); // Set the stage as the input processor
        stage.addActor(arrowLeftButton);
        stage.addActor(arrowRightButton);
    }

    @Override
    public void create() {

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
        tileHeight = (int) (tileTexture.getHeight() * SCALE_FACTOR);
        tileWidth = (int) (tileTexture.getWidth() * SCALE_FACTOR);
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

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1); // Light blue

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
        if (backgroundY < -tileTexture.getHeight() * BACKGROUND_SCROLL_SPEED) {
            backgroundY = 0;
        }

        updateBlinkingEffect(deltaTime);
        handleControls();

        if (TimeUtils.nanoTime() - lastDropTime > ONE_SECOND_NS) {
            spawnRaindrop();
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
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
        Rectangle rectangle = new Rectangle();
        rectangle.x = MathUtils.random(0, WIDTH - BUCKET_WIDTH);
        rectangle.y = HEIGHT;
        rectangle.width = RAINDROP_WIDTH;
        rectangle.height = RAINDROP_HEIGHT;

        int randomInt = MathUtils.random(0, 12);
        Texture texture = new Texture(Gdx.files.internal("textures/droplet-" + randomInt + ".png"));

        Raindrop raindrop = new Raindrop(rectangle, texture);

        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    private void renderBackground(SpriteBatch batch) {
        float scaledHeight = tileTexture.getHeight() * SCALE_FACTOR;
        float scaledWidth = tileTexture.getWidth() * SCALE_FACTOR;

        for (float x = LEFT_OF_SCREEN; x < WIDTH; x += scaledWidth) {
            for (float y = backgroundY; y < HEIGHT; y += scaledHeight) {
                batch.draw(tileTexture, x, y, scaledWidth, scaledHeight);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        // Update the camera with the new window size
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        // Update the stage's viewport as well, if you are using Scene2D UI
        stage.getViewport().update(width, height, true);

        // Call a method to update the button bounds
        updateButtonBounds();
    }

    private void updateButtonBounds() {
        leftButtonBounds.set(arrowLeftButton.getX(), arrowLeftButton.getY(),
                arrowLeftButton.getWidth(), arrowLeftButton.getHeight());
        rightButtonBounds.set(arrowRightButton.getX(), arrowRightButton.getY(),
                arrowRightButton.getWidth(), arrowRightButton.getHeight());
    }

    @Override
    public void dispose() {
        for (Texture texture : dropletTextures) {
            texture.dispose();
        }
        arrowLeftTexture.dispose();
        arrowRightTexture.dispose();
        stage.dispose();
        tileTexture.dispose();
        bucketTexture.dispose();
        dropletSound.dispose();
        rainMusic.dispose();
        batch.dispose();
        greyDropTexture.dispose();
    }
}
