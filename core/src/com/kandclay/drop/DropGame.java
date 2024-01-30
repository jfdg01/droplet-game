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

    private long lastDropTime;

    // Extra
    private Music rainMusic;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Vector3 touchPos;

    @Override
    public void create() {

        raindrops = new Array<>();
        dropletImgs = new Array<>();

        // Assets
        for (int i = 0; i < 13; i++) {
            int randomNumber = MathUtils.random(0, 12);
            Texture randomImg = new Texture(Gdx.files.internal("textures/droplet-" + randomNumber + ".png"));
            Rectangle rectangle = new Rectangle();
            Raindrop raindrop = new Raindrop(rectangle, randomImg);
            raindrops.add(raindrop);
        }
        bucketImg = new Texture(Gdx.files.internal("textures/bucket.png"));

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

        raindrops = new Array<>();
        spawnRaindrop();

        touchPos = new Vector3();
    }

    public void handleLeftKey(float deltaTime, float moveAmount) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (bucket.x - moveAmount < LEFT_WALL) {
                bucket.x = 0;
            } else {
                bucket.x -= moveAmount;
            }
        }
    }

    public void handleRightKey(float deltaTime, float moveAmount) {
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
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();

        // Draw the assets according to their current position
        batch.begin();
        batch.draw(bucketImg, bucket.x, bucket.y);
        for (Raindrop raindrop : raindrops) {
            float x = raindrop.getRectangle().x;
            float y = raindrop.getRectangle().y;
            batch.draw(raindrop.getTexture(), x, y);
        }
        batch.end();

        float deltaTime = Gdx.graphics.getDeltaTime();
        float moveAmount = BUCKET_MOVE_SPEED * deltaTime;

        handleLeftKey(deltaTime, moveAmount);
        handleRightKey(deltaTime, moveAmount);
        handleTouchInputBucket();


        if (TimeUtils.nanoTime() - lastDropTime > ONE_SECOND_NS) {
            spawnRaindrop();
        }

        // Update raindrop position
        for (Iterator<Raindrop> iter = raindrops.iterator(); iter.hasNext(); ) {
            Raindrop raindrop = iter.next();
            raindrop.getRectangle().y -= RAINDROP_FALL_SPEED * Gdx.graphics.getDeltaTime();
            if (raindrop.getRectangle().y + RAINDROP_WIDTH < 0) iter.remove();
            if (raindrop.getRectangle().overlaps(bucket)) {
                dropletSound.play();
                iter.remove();
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
    }

}
