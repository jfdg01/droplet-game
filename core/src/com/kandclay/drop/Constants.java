package com.kandclay.drop;

public class Constants {

    // Constants
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    public static final int ONE_SECOND_NS = 1_000_000_000;
    public static final int BUCKET_WIDTH = 64;
    public static final int BUCKET_HEIGHT = 20;
    public static final int RAINDROP_WIDTH = 64;
    public static final int RAINDROP_HEIGHT = 64;
    public static final int BUCKET_INITIAL_Y = 20;
    public static final int BUCKET_MOVE_SPEED = 300;
    public static final int RAINDROP_FALL_SPEED = 200;
    public static final int BOTTOM_OF_SCREEN = 0;
    public static final int TOP_OF_SCREEN = HEIGHT;
    public static final int LEFT_OF_SCREEN = 0;
    public static final int RIGHT_OF_SCREEN = WIDTH;
    public static final float BLINKING_TIME = 0.25f;
    public static final int MAX_TIMES_BLINKED = 3;
    public static final float SCALE_FACTOR = 0.6f;
    public static final float BACKGROUND_SCROLL_SPEED = 120f;
    public static final int PADDING_PIXELS = 20;
    public static final int PADDING_BETWEEN_BUTTONS = 20;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int BUTTON_WIDTH = 180;
    public static final int BUTTON_HEIGHT = 80;
    public static final int LEFT_BUTTON_X = LEFT_OF_SCREEN + PADDING_PIXELS;
    public static final int RIGHT_BUTTON_X = WIDTH - BUTTON_WIDTH - PADDING_PIXELS;
    public static final int LEFT_BUTTON_Y = TOP_OF_SCREEN - PADDING_PIXELS - BUTTON_HEIGHT;
    public static final int RIGHT_BUTTON_Y = TOP_OF_SCREEN - PADDING_PIXELS - BUTTON_HEIGHT;

    public static final int PLAY_BUTTON_Y = HEIGHT/2;
    public static final int EXIT_BUTTON_Y = HEIGHT/2 - BUTTON_HEIGHT - PADDING_BETWEEN_BUTTONS;
    public static final int PLAY_BUTTON_X = WIDTH/2 - BUTTON_WIDTH/2;
    public static final int EXIT_BUTTON_X = WIDTH/2 - BUTTON_WIDTH/2;

}
