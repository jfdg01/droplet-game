package com.kandclay.drop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Raindrop {

    private Rectangle rectangle;
    private Texture texture;

    public Raindrop(Rectangle rectangle, Texture texture) {
        this.rectangle = rectangle;
        this.texture = texture;
    }

    //getters
    public Rectangle getRectangle() {
        return rectangle;
    }

    public Texture getTexture() {
        return texture;
    }

    // Setter for rectangle x and y
    public void setRectangleX(float x) {
        rectangle.x = x;
    }
    public void setRectangleY(float y) {
        rectangle.y = y;
    }

}
