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

    private TextureRegion playerStand;
    private TextureRegion bigPlayerStand;
    private Animation<TextureRegion> playerJump;
    private TextureRegion bigPlayerJump;
    private Animation<TextureRegion> playerRun;
    private Animation<TextureRegion> bigPlayerRun;
    private Animation<TextureRegion> growPlayer;

    private float stateTimer;
    private boolean runningRight;
    private boolean playerIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigPlayer;

    public Player(PlayScreen screen){

        //initialize default values
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;



        Array<TextureRegion> frames = new Array<>();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas2().findRegion("ls_walkright"), i * 32, 0, 32, 64));
        playerRun = new Animation(0.1f, frames);
        frames.clear();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"),i * 16, 0, 16, 32));
        bigPlayerRun = new Animation(0.1f, frames);
        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas2().findRegion("ls_walkdown"), 0, 0,32,64));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0,16,32));
        frames.add(new TextureRegion(screen.getAtlas2().findRegion("ls_walkdown"), 0, 0,32,64));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0,16,32));
        growPlayer = new Animation(0.2f, frames);
        frames.clear();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas2().findRegion("ls_walkright"), i * 32, 0, 32, 64));
        playerJump = new Animation(0.1f, frames);
        frames.clear();

        bigPlayerJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"),80,0, 16,32);

        playerStand = new TextureRegion(screen.getAtlas2().findRegion("ls_walkright"), 0, 0, 32, 64);
        bigPlayerStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"),0,0, 16,32);

        definePlayer();
        setBounds(0, 0, 32 / LightSkull.PPM, 64 / LightSkull.PPM);
        setRegion(playerStand);
    }

    public void update(float dt){
        if(playerIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6/LightSkull.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6/LightSkull.PPM);
            setRegion(getFrame(dt));
                if(timeToDefineBigPlayer){
                    defineBigPlayer();
        }
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch(currentState){
            case GROWING:
                region = (TextureRegion) growPlayer.getKeyFrame(stateTimer);
                if(growPlayer.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = playerIsBig ? bigPlayerJump : (TextureRegion) playerJump.getKeyFrame(stateTimer,true);
                break;
            case RUNNING:
                region = playerIsBig ? (TextureRegion) bigPlayerRun.getKeyFrame(stateTimer, true) : (TextureRegion) playerRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = playerIsBig ? bigPlayerStand : playerStand;
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
        playerIsBig = true;
        timeToDefineBigPlayer = true;
        setBounds(getX(),getY(),getWidth(),getHeight());
        LightSkull.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void definePlayer(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / LightSkull.PPM, 333 / LightSkull.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(20 / LightSkull.PPM);
        fdef.filter.categoryBits = LightSkull.LIGHTSKULL_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.COIN_BIT | LightSkull.BRICK_BIT | LightSkull.ENEMY_BIT | LightSkull.OBJECT_BIT | LightSkull.ENEMY_HEAD_BIT | LightSkull.ITEM_BIT;

        fdef.shape = shape;
        shape.setPosition(new Vector2(0, 3 / LightSkull.PPM));
        b2body.createFixture(fdef).setUserData(this);


        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / LightSkull.PPM, 23 / LightSkull.PPM), new Vector2(2 / LightSkull.PPM, 23 / LightSkull.PPM));
        fdef.filter.categoryBits = LightSkull.LIGHTSKULL_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void defineBigPlayer(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0,10/ LightSkull.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(15 / LightSkull.PPM);
        fdef.filter.categoryBits = LightSkull.LIGHTSKULL_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.COIN_BIT | LightSkull.BRICK_BIT | LightSkull.ENEMY_BIT | LightSkull.OBJECT_BIT | LightSkull.ENEMY_HEAD_BIT | LightSkull.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / LightSkull.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / LightSkull.PPM, 15 / LightSkull.PPM), new Vector2(2 / LightSkull.PPM, 15 / LightSkull.PPM));
        fdef.filter.categoryBits = LightSkull.LIGHTSKULL_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigPlayer = false;
    }
    public boolean isBig(){
        return playerIsBig;
    }
}
