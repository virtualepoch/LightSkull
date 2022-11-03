package com.virtualepoch.game.Sprites.Other;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet {

    public static final int SPEED = 1;
    private static Texture texture;

    float x, y;

    public boolean remove = false;

    public Bullet (float x, float y){
        this.x = x;
        this.y = y;

        if(texture == null)
            texture = new Texture("bullet.png");
    }

    public void update (float dt) {
        x += SPEED * dt;
        if(x > Gdx.graphics.getWidth())
            remove = true;
    }

    public void renderBullet (SpriteBatch batch){
        batch.draw(texture, x, y);
    }
}
