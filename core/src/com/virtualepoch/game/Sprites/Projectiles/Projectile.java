package com.virtualepoch.game.Sprites.Projectiles;

import static com.virtualepoch.game.Sprites.Projectiles.Projectile.State.IDLE;
import static com.virtualepoch.game.Sprites.Projectiles.Projectile.State.MOVING_RIGHT_LEFT;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Enemies.Enemy;
import com.virtualepoch.game.Sprites.Player;

public abstract class Projectile extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Body body;
    protected Player player;

    protected Vector2 velocity;
    protected Vector2 velocityNeg;

    public enum State { MOVING_RIGHT_LEFT, IDLE }
    public State currentState;
    public State previousState;

    protected boolean toDestroy;
    protected boolean destroyed;
    protected boolean movingRight;

    public Projectile(PlayScreen screen, float x, float y){
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.IDLE;
        previousState = State.IDLE;

        setPosition(x,y);
//        setBounds(getX(),getY(),30/ LightSkull.PPM,10/ LightSkull.PPM);
        defineProjectile();
        toDestroy = false;
        destroyed = false;
        movingRight = true;
    }

    public abstract void defineProjectile();

    public State getState(){
        if(body.getLinearVelocity().x != 0)
            return MOVING_RIGHT_LEFT;
        else return IDLE;
    }

    public abstract void hitEnemy(Enemy enemy);

    public abstract void hitObject();

    public void draw(Batch batch){
        if(!destroyed)
            super.draw(batch);
    }

    public void destroy(){
        toDestroy = true;
    }

    public void reverseVelocity(){
            velocity.x = -velocity.x;
    }

    public void resetVelocity(){
        movingRight = true;
    }

    public void update(float dt){
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
        }
    }
}

