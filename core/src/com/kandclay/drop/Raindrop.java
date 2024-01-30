package com.kandclay.drop;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Raindrop {

    private Rectangle rectangle;
    private TextureRegion texture;

    public Raindrop(Rectangle rectangle, TextureRegion texture) {
        this.rectangle = rectangle;
        this.texture = texture;
    }

    //getters
    public Rectangle getRectangle() {
        return rectangle;
    }

    public TextureRegion getTexture() {
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
