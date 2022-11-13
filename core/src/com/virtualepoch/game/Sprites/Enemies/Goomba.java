package com.virtualepoch.game.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Player;

public class Goomba extends Enemy {

    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        Array<TextureRegion> frames = new Array<>();
        for(int i = 0; i < 11; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("skull_fly" ), i * 40, 0, 40, 40));
        walkAnimation = new Animation(0.2f, frames);
        frames.clear();
        stateTime = 0;
        setBounds(getX(), getY(), 60 / LightSkull.PPM, 60 / LightSkull.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float dt){
        stateTime +=dt;
        if(setToDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("spider_dead" ), 0, 0, 17, 13));
            stateTime = 0;
        }
        else if(!destroyed) {
            body.setLinearVelocity(velocity);
            setPosition(body.getPosition().x - getWidth() / 2 + 4 / LightSkull.PPM, body.getPosition().y - getHeight() / 2 + 3 / LightSkull.PPM);
            setRegion(walkAnimation.getKeyFrame(stateTime,true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(23 / LightSkull.PPM);
        fdef.filter.categoryBits = LightSkull.ENEMY_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.OBJECT_BIT | LightSkull.BRICK_BIT | LightSkull.COIN_BIT | LightSkull.PLAYER_BIT  | LightSkull.ENEMY_BIT | LightSkull.PROJECTILE_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);

        //Create the Head here:
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-10, 10).scl(1 / LightSkull.PPM);
        vertice[1] = new Vector2(10, 10).scl(1 / LightSkull.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / LightSkull.PPM);
        vertice[3] = new Vector2(-3, 3).scl(1 / LightSkull.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = LightSkull.ENEMY_HEAD_BIT;
        body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch){
        if(!destroyed || stateTime < 1)
            super.draw(batch);
    }

    public void onEnemyHit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.INJURED)
            setToDestroy = true;
        else
            reverseVelocity(true,false);

    }

    @Override
    public void hitByLaser(Player player) {
        setToDestroy = true;
        LightSkull.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }
}
