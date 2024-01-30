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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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
    boolean isLeftPressed = false;
    boolean isRightPressed = false;

    // Stage
    Stage stage;

    @Override
    public void create() {

        raindrops = new Array<>();
        dropletTextures = new Array<>();


        // Raindrops
        for (int i = 0; i < 13; i++) {
            int randomNumber = MathUtils.random(0, 12);
            Texture randomImg = new Texture(Gdx.files.internal("textures/droplet-" + randomNumber + ".png"));
            Rectangle rectangle = new Rectangle();
            Raindrop raindrop = new Raindrop(rectangle, randomImg);
            raindrops.add(raindrop);
        }

        // Images
        bucketTexture = new Texture(Gdx.files.internal("textures/bucket.png"));
        greyDropTexture = new Texture(Gdx.files.internal("textures/droplet-greyscale.png"));
        tileTexture = new Texture(Gdx.files.internal("textures/rain.jpg"));

        arrowLeftTexture = new Texture(Gdx.files.internal("textures/arrow-left.png"));
        arrowRightTexture = new Texture(Gdx.files.internal("textures/arrow-right.png"));

        Image arrowLeftButton = new Image(new TextureRegionDrawable(new TextureRegion(arrowLeftTexture)));
        Image arrowRightButton = new Image(new TextureRegionDrawable(new TextureRegion(arrowRightTexture)));

        // Set the size of the buttons
        float buttonSize = BUCKET_WIDTH; // Same size as the bucket
        arrowLeftButton.setSize(buttonSize, buttonSize);
        arrowRightButton.setSize(buttonSize, buttonSize);

        // Position the buttons at the top center of the screen
        float buttonY = HEIGHT - buttonSize - PADDING_TOP_PIXELS; // 20 pixels from the top
        float centerX = (float) WIDTH / 2;
        arrowLeftButton.setPosition(centerX - buttonSize - PADDING_BETWEEN_BUTTONS, buttonY); // 10 pixels apart
        arrowRightButton.setPosition(centerX + PADDING_BETWEEN_BUTTONS, buttonY);
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


        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set the stage as the input processor

        stage.addActor(arrowLeftButton);
        stage.addActor(arrowRightButton);

        // Sounds
        dropletSound = Gdx.audio.newSound(Gdx.files.internal("sounds/drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/rain.mp3"));

        rainMusic.setLooping(true);
        rainMusic.play();

        // Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        // Batch
        batch = new SpriteBatch();

        // Objects
        bucket = new Rectangle();
        bucket.x = (float) HEIGHT / 2 - (float) BUCKET_WIDTH / 2;
        bucket.y = BUCKET_INITIAL_Y;
        bucket.width = BUCKET_WIDTH;
        bucket.height = BUCKET_HEIGHT;

        tileHeight = (int) (tileTexture.getHeight() * SCALE_FACTOR);
        tileWidth = (int) (tileTexture.getWidth() * SCALE_FACTOR);

        raindrops = new Array<Raindrop>();
        spawnRaindrop();

        touchPos = new Vector3();
    }

    public void handleMovementKey(float moveAmount, int direction) {
        if (direction == LEFT) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (bucket.x - moveAmount < LEFT_OF_SCREEN) {
                    bucket.x = 0;
                } else {
                    bucket.x -= moveAmount;
                }
            }
        }

        if (direction == RIGHT) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (bucket.x + moveAmount > RIGHT_OF_SCREEN - BUCKET_WIDTH) {
                    bucket.x = RIGHT_OF_SCREEN - BUCKET_WIDTH;
                } else {
                    bucket.x += moveAmount;
                }
            }
        }
    }

    private void handleMovementButton(float bucketMoveAmount, int direction) {
        if (direction == LEFT) {
            if (bucket.x - bucketMoveAmount < LEFT_OF_SCREEN) {
                bucket.x = 0;
            } else {
                bucket.x -= bucketMoveAmount;
            }
        }

        if (direction == RIGHT) {
            if (bucket.x + bucketMoveAmount > RIGHT_OF_SCREEN - BUCKET_WIDTH) {
                bucket.x = RIGHT_OF_SCREEN - BUCKET_WIDTH;
            } else {
                bucket.x += bucketMoveAmount;
            }
        }
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
        float bucketMoveAmount = BUCKET_MOVE_SPEED * deltaTime;

        if (isLeftPressed) {
            handleMovementButton(bucketMoveAmount, LEFT);
        }
        if (isRightPressed) {
            handleMovementButton(bucketMoveAmount, RIGHT);
        }

        backgroundY -= (int) (BACKGROUND_SCROLL_SPEED * deltaTime);
        if (backgroundY < -tileTexture.getHeight() * BACKGROUND_SCROLL_SPEED) {
            backgroundY = 0;
        }

        updateBlinkingEffect(deltaTime);
        handleMovementKey(bucketMoveAmount, LEFT);
        handleMovementKey(bucketMoveAmount, RIGHT);

        // handleTouchInputBucket();

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


    private void handleTouchInputBucket() {
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - (float) BUCKET_WIDTH / 2;
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
