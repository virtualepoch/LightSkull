package com.virtualepoch.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;

public class Bullet extends Sprite {
    protected PlayScreen screen;
    protected World world;
    protected Body body;
    protected Player player;

    protected Vector2 velocity;

    protected boolean toDestroy;
    protected boolean destroyed;
    protected boolean movingRight;

    private float stateTime;
    private static Animation<TextureRegion> animation;

    public Bullet(PlayScreen screen, float x, float y){
        this.screen = screen;
        this.world = screen.getWorld();

        setPosition(x,y);

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 8; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i * 41, 0, 41, 32));
        animation = new Animation<>(0.05f, frames);
        frames.clear();
        // POSITION AND WIDTH AND HEIGHT OF SPRITE ON SCREEN
        setBounds(getX(), getY(), 40 / LightSkull.PPM, 8 / LightSkull.PPM);

        stateTime = 0;
        velocity = new Vector2(5f, 0);

        toDestroy = false;
        destroyed = false;
        movingRight = true;

        defineBullet();
    }
    public void defineBullet(){
        BodyDef bDef = new BodyDef();
        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / LightSkull.PPM);
        fDef.filter.categoryBits = LightSkull.PROJECTILE_BIT;
        fDef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.ENEMY_BIT;

        fDef.shape = shape;
        body.createFixture(fDef).setUserData(this);
    }

    public TextureRegion getFrame(float dt){
        TextureRegion region = (TextureRegion) animation.getKeyFrame(stateTime, true);

        if((body.getLinearVelocity().x < 0 || !movingRight) && !region.isFlipX()){
            region.flip(true, false);
            movingRight = false;
        }else if ((body.getLinearVelocity().x > 0 || movingRight) && region.isFlipX()){
            region.flip(true, false);
            movingRight = true;
        }
        return region;
    }

//    public boolean isOutOfScreen() {
//        return body.getPosition().x > player.getX() + 400 / LightSkull.PPM;
//    }


    public boolean moving(){
        return body.getLinearVelocity().x != 0;
    }

    public void draw(Batch batch){
        if(!destroyed)
            super.draw(batch);
    }

    public void destroy(){
        toDestroy = true;
    }

    public boolean destroyed() {
      if(destroyed) {
          return true;
      }
        return false;
    }

    public void reverseVelocity(){
        velocity.x = -velocity.x;
    }

    public void update(float dt) {
        stateTime +=dt;
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            body = null;
            destroyed = true;
            stateTime = 0;
        }
        else if (!destroyed){
            body.setLinearVelocity(velocity);
            velocity.y = body.getLinearVelocity().y;

            setRegion(getFrame(dt));

            // THIS IS WHERE YOU CAN CHANGE THE SPRITE IMAGE POSITION ON THE OBJECTS B2BODY //
            if(velocity.x < 0)
                setPosition(body.getPosition().x - getWidth() / 5f, body.getPosition().y - getHeight() / 2);
            else
                setPosition(body.getPosition().x - getWidth() / 1.3f, body.getPosition().y - getHeight() / 2);
        }
    }
}