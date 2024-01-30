package com.kandclay.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import static com.kandclay.drop.Constants.*;

import java.util.Iterator;

public class DropGame extends ApplicationAdapter {

    // Bucket
    private Rectangle bucket;
    private Texture bucketImg;

    // Raindrops
    private Array<Raindrop> raindrops;
    private Array<Texture> dropletImgs;
    private Sound dropletSound;
    Array<MissedRaindrop> missedRaindrops = new Array<>();
    private long lastDropTime;


    private Texture greyscaleDropImg;
    private Texture photo;


    // Extra
    private Music rainMusic;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Vector3 touchPos;

    @Override
    public void create() {

        raindrops = new Array<Raindrop>();
        dropletImgs = new Array<Texture>();


        // Raindrops
        for (int i = 0; i < 13; i++) {
            int randomNumber = MathUtils.random(0, 12);
            Texture randomImg = new Texture(Gdx.files.internal("textures/droplet-" + randomNumber + ".png"));
            Rectangle rectangle = new Rectangle();
            Raindrop raindrop = new Raindrop(rectangle, randomImg);
            raindrops.add(raindrop);
        }

        // Images
        bucketImg = new Texture(Gdx.files.internal("textures/bucket.png"));
        greyscaleDropImg = new Texture(Gdx.files.internal("textures/droplet-greyscale.png"));
        photo = new Texture(Gdx.files.internal("textures/fd.png"));

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

        raindrops = new Array<Raindrop>();
        spawnRaindrop();

        touchPos = new Vector3();
    }

    public void handleLeftKey(float moveAmount) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (bucket.x - moveAmount < LEFT_WALL) {
                bucket.x = 0;
            } else {
                bucket.x -= moveAmount;
            }
        }
    }

    public void handleRightKey(float moveAmount) {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (bucket.x + moveAmount > RIGHT_WALL - BUCKET_WIDTH) {
                bucket.x = RIGHT_WALL - BUCKET_WIDTH;
            } else {
                bucket.x += moveAmount;
            }
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0.2f, 1); // Light blue

        camera.update();

        // Draw the assets according to their current position
        batch.begin();
        batch.draw(photo, LEFT_OF_SCREEN, BOTTOM_OF_SCREEN, WIDTH, HEIGHT);
        batch.draw(bucketImg, bucket.x, bucket.y);
        for (Raindrop raindrop : raindrops) {
            float x = raindrop.getRectangle().x;
            float y = raindrop.getRectangle().y;
            batch.draw(raindrop.getTexture(), x, y);
        }
        batch.end();

        updateRaindropPosition();
        renderMissedRaindrops();

        float deltaTime = Gdx.graphics.getDeltaTime();
        float moveAmount = BUCKET_MOVE_SPEED * deltaTime;

        updateBlinkingEffect(deltaTime);
        handleLeftKey(moveAmount);
        handleRightKey(moveAmount);
        handleTouchInputBucket();

        if (TimeUtils.nanoTime() - lastDropTime > ONE_SECOND_NS) {
            spawnRaindrop();
        }
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
                batch.draw(greyscaleDropImg, missed.x, BOTTOM_OF_SCREEN);
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

    @Override
    public void dispose() {
        for (Texture texture : dropletImgs) {
            texture.dispose();
        }
        bucketImg.dispose();
        dropletSound.dispose();
        rainMusic.dispose();
        batch.dispose();
        greyscaleDropImg.dispose();
    }
}
