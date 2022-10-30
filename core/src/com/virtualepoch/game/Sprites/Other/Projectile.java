package com.virtualepoch.game.Sprites.Other;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Player;

public abstract class Projectile extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Player player;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body body;

    public Projectile(PlayScreen screen, float x, float y){
        this.screen = screen;
        this.world = screen.getWorld();
        this.player = player;
        setPosition(x,y);
        setBounds(getX(),getY(),16/ LightSkull.PPM,16/ LightSkull.PPM);
        defineProjectile();
        toDestroy = false;
        destroyed = false;
    }
    public abstract void defineProjectile();
    public abstract void fire();

    public void update(float dt){
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
        }
    }

    public void draw(Batch batch){
        if(!destroyed)
            super.draw(batch);
    }
    public void destroy(){
        toDestroy = true;
    }
    public void reverseVelocity(boolean x, boolean y){
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}
