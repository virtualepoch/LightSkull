package com.virtualepoch.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;


public class Player extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING };
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private TextureRegion bigMarioStand;
    private TextureRegion marioJump;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> marioRun;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;

    public Player(PlayScreen screen){

        //initialize default values
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;



        Array<TextureRegion> frames = new Array<>();
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, -1, 16, 17));
        marioRun = new Animation(0.1f, frames);
        frames.clear();
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),i * 16, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0,16,32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0,16,32));
        growMario = new Animation(0.2f, frames);
        frames.clear();

        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"),80,-1,16,17);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"),80,0, 16,32);

        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, -1, 16, 17);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0, 16,32);

        defineMario();
        setBounds(0, 0, 16 / LightSkull.PPM, 16 / LightSkull.PPM);
        setRegion(marioStand);
    }

    public void update(float dt){
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch(currentState){
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ? (TextureRegion) bigMarioRun.getKeyFrame(stateTimer, true) : (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public State getState(){

        if(runGrowAnimation)
            return State.GROWING;
        else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void grow(){
        runGrowAnimation = true;
        marioIsBig = true;
        setBounds(getX(),getY(),getWidth(),getHeight() * 2);
        LightSkull.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / LightSkull.PPM, 222 / LightSkull.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(7 / LightSkull.PPM);
        fdef.filter.categoryBits = LightSkull.LUCKY_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.COIN_BIT | LightSkull.BRICK_BIT | LightSkull.ENEMY_BIT | LightSkull.OBJECT_BIT | LightSkull.ENEMY_HEAD_BIT | LightSkull.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / LightSkull.PPM, 6 / LightSkull.PPM), new Vector2(2 / LightSkull.PPM, 6 / LightSkull.PPM));
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData("head");
    }
}
