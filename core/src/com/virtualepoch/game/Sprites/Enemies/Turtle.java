package com.virtualepoch.game.Sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Player;

public class Turtle extends Enemy {
    public enum State {WALKING, INJURED, DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private TextureRegion injured;
    private Animation<TextureRegion> walkAnimation;
    private boolean destroyed;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        // here I had to create a new variable to update the child objects velocity
        Vector2 velocity2 = new Vector2(0.5f, -1);
        velocity = velocity2;

        injured = new TextureRegion(screen.getAtlas().findRegion("monster_crawl"),0,0,90,59);

        Array<TextureRegion> frames = new Array<>();
        for(int i = 0; i < 11; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("monster_crawl"),i * 90,0,90,59));
        walkAnimation = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        currentState = previousState = State.WALKING;

        // CODE BELOW SETS THE SIZE OF THE SPRITE RENDERED ON THE PLAYSCREEN
        setBounds(getX(),getY(),80 / LightSkull.PPM, 80 / LightSkull.PPM);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        PolygonShape shape = new PolygonShape();

        fdef.shape = shape;
        fdef.filter.categoryBits = LightSkull.ENEMY_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.OBJECT_BIT | LightSkull.BRICK_BIT | LightSkull.COIN_BIT | LightSkull.PLAYER_BIT  | LightSkull.ENEMY_BIT | LightSkull.PROJECTILE_BIT;

        shape.setAsBox(30 / LightSkull.PPM, 28 / LightSkull.PPM);
        body.createFixture(fdef).setUserData(this);

        //Create the Head here:
//        PolygonShape head = new PolygonShape();
//        Vector2[] vertice = new Vector2[4];
//        vertice[0] = new Vector2(-20, 11).scl(1 / LightSkull.PPM);
//        vertice[1] = new Vector2(20, 11).scl(1 / LightSkull.PPM);
//        vertice[2] = new Vector2(-3, 3).scl(1 / LightSkull.PPM);
//        vertice[3] = new Vector2(-3, 3).scl(1 / LightSkull.PPM);
//        head.set(vertice);
//
//        fdef.shape = head;
//        fdef.restitution = 1.5f;
//        fdef.filter.categoryBits = LightSkull.ENEMY_HEAD_BIT;
//        body.createFixture(fdef).setUserData(this);
    }

    public void draw(Batch batch){
        if(!destroyed || stateTime < 0)
            super.draw(batch);
    }

    public void onEnemyHit(Enemy enemy){
        if(enemy instanceof Turtle){
            if(((Turtle) enemy).currentState == State.INJURED && currentState != State.INJURED){
                currentState = State.DEAD;
            }
            else if(currentState == State.INJURED)
                return;
            else
                reverseVelocity(true,false);

        }
        else if(currentState != State.INJURED)
            reverseVelocity(true,false);
    }

    public TextureRegion getFrame(float dt){
        TextureRegion region;

        switch (currentState) {
            case INJURED:
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
        if(currentState == State.INJURED && stateTime > 5){
            currentState = State.WALKING;
        }
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        if(currentState == State.DEAD && stateTime > 0 && !destroyed){ // NUMBER ON THIS LINE CONTROLS HOW LONG TURTLE IS ON SCREEN FOR AFTER DIEING
                world.destroyBody(body);
                destroyed = true;
        }
        else
            body.setLinearVelocity(velocity);
    }

    @Override
    public void hitByLaser(Player player) {
        currentState = State.DEAD;
        LightSkull.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    public State getCurrentState(){
        return currentState;
    }
}
