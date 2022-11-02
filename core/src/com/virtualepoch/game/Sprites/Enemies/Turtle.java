package com.virtualepoch.game.Sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Player;

public class Turtle extends Enemy {
    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public enum State {WALKING, INJURED_STANDING, INJURED_KICKED, DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private TextureRegion injured;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private float deadRotationDegrees;
    private boolean destroyed;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        // here I had to create a new variable to update the child objects velocity
        Vector2 velocity2 = new Vector2(0.5f, -1);
        velocity = velocity2;

        injured = new TextureRegion(screen.getAtlas().findRegion("monster_crawl"),0,0,90,59);

        frames = new Array<TextureRegion>();
        for(int i = 0; i < 11; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("monster_crawl"),i * 90,0,90,59));
        walkAnimation = new Animation<TextureRegion>(0.07f, frames);

        currentState = previousState = State.WALKING;
        deadRotationDegrees = 0;

        // VVV === THIS IS THE SIZE OF THE SPRITE RENDERED ON THE PLAYSCREEN
        setBounds(getX(),getY(),80 / LightSkull.PPM, 60 / LightSkull.PPM);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(10 / LightSkull.PPM);
        fdef.filter.categoryBits = LightSkull.ENEMY_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.COIN_BIT | LightSkull.BRICK_BIT | LightSkull.ENEMY_BIT | LightSkull.OBJECT_BIT | LightSkull.PLAYER_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //Create the Head here:
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-22, 11).scl(1 / LightSkull.PPM);
        vertice[1] = new Vector2(22, 11).scl(1 / LightSkull.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / LightSkull.PPM);
        vertice[3] = new Vector2(-3, 3).scl(1 / LightSkull.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 1.5f;
        fdef.filter.categoryBits = LightSkull.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch){
        if(!destroyed || stateTime < 1)
            super.draw(batch);
    }

    public void onEnemyHit(Enemy enemy){
        if(enemy instanceof Turtle){
            if(((Turtle) enemy).currentState == State.INJURED_KICKED && currentState != State.INJURED_KICKED){
                killed();
            }
            else if(currentState == State.INJURED_KICKED)
                return;
            else
                reverseVelocity(true,false);

        }
        else if(currentState != State.INJURED_KICKED)
            reverseVelocity(true,false);
    }

    public TextureRegion getFrame(float dt){
        TextureRegion region;

        switch (currentState) {
            case INJURED_STANDING:
            case  INJURED_KICKED:
                region = injured;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if(velocity.x > 0 && region.isFlipX() == false){
            region.flip(true,false);
        }
        if(velocity.x < 0 && region.isFlipX() == true){
            region.flip(true,false);
        }
        // if the current state is the same as the previous state increase the state timer.
        // otherwise the state has changed and we need to reset timer.
        stateTime = currentState == previousState ? stateTime + dt : 0;
        // update previous state
        previousState = currentState;
        // return our final adjusted frame
        return region;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if(currentState == State.INJURED_STANDING && stateTime > 5){
            currentState = State.WALKING;
        }
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 16 / LightSkull.PPM);

        if(currentState == State.DEAD){
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if(stateTime > 5 && !destroyed){
                world.destroyBody(b2body);
                destroyed = true;
            }
        }
        else
            b2body.setLinearVelocity(velocity);
    }

    @Override
    public void hitOnHead(Player player) {
        if(currentState != State.INJURED_STANDING) {
            currentState = State.INJURED_STANDING;
            velocity.x = 0;
        } else {
            kick(player.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    public void kick(int speed){
        velocity.x = speed;
        currentState = State.INJURED_KICKED;
    }

    public State getCurrentState(){
        return currentState;
    }

    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = LightSkull.NOTHING_BIT;

        for(Fixture fixture : b2body.getFixtureList())
            fixture.setFilterData(filter);
        b2body.applyLinearImpulse(new Vector2(0, 5f),b2body.getWorldCenter(), true);

    }
}
