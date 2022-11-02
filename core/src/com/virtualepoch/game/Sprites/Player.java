package com.virtualepoch.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Enemies.Enemy;
import com.virtualepoch.game.Sprites.Enemies.Turtle;

import java.awt.Event;


public class Player extends Sprite {
    public Vector2 velocity;

    public enum State { FALLING, JUMPING, STANDING, MOVING_RIGHT_LEFT, MOVING_UP, MOVING_DOWN, GROWING, DEAD };
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;

    private Animation<TextureRegion> playerStand;
    private Animation<TextureRegion> playerMoveRightLeft;
    private Animation<TextureRegion> playerMoveUp;
    private Animation<TextureRegion> playerMoveDown;
    private Animation<TextureRegion> playerJump;
    private Animation<TextureRegion> playerDead;
    private Animation<TextureRegion> growPlayer;

    private TextureRegion bigPlayerStand;
    private Animation<TextureRegion> bigPlayerJump;
    private Animation<TextureRegion> bigPlayerRun;

    private float stateTimer;
    private boolean movingRight;
    public boolean movingUp;
    public boolean movingDown;
    private boolean playerIsBig;
    private boolean runGrowAnimation;
    private boolean timeToDefineBigPlayer;
    private boolean timeToRedefinePlayer;
    private boolean playerIsDead;
    
    public boolean falling;

    private int lightskullRunWidth;
    private int lightskullRunHeight;
    
    public float firstY;

    public Player(PlayScreen screen){

        //initialize default values
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        movingRight = true;
        movingUp = false;
        movingDown = false;
        lightskullRunWidth = 53;
        lightskullRunHeight = 51;

//////////////////////////////////////// VVV === SPRITE SHEET SECTION === VVV ////////////////////////////////////////
        Array<TextureRegion> frames = new Array<>();
        for(int i = 0; i < 2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_stand"), i * 45, 0, 45, 56));
        playerStand = new Animation(0.8f, frames);
        frames.clear();
        for(int i = 0; i < 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"), i * lightskullRunWidth, 0, lightskullRunWidth, lightskullRunHeight));
        playerMoveRightLeft = new Animation(0.1f, frames);
        frames.clear();
        for(int i = 0; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"), i * lightskullRunWidth, 0, lightskullRunWidth, lightskullRunHeight));
        playerMoveUp = new Animation(0.1f, frames);
        frames.clear();
        for(int i = 0; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"), i * lightskullRunWidth, 0, lightskullRunWidth, lightskullRunHeight));
        playerMoveDown = new Animation(0.1f, frames);
        frames.clear();
        for(int i = 0; i < 5; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"), i * 53, 0, lightskullRunWidth, lightskullRunHeight));
        playerJump = new Animation(0.1f, frames);
        frames.clear();
        for(int i = 0; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_stand"), i * 45, 0, 45, 56));
        playerDead = new Animation(0.4f, frames);
        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"), 0, 0, lightskullRunWidth, lightskullRunHeight));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"), 0, 0, lightskullRunWidth, lightskullRunHeight));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"), 0, 0, lightskullRunWidth, lightskullRunHeight));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"), 0, 0, lightskullRunWidth, lightskullRunHeight));
        growPlayer = new Animation(0.2f, frames);
        frames.clear();

        ///////////////////////// VVV === BIG OR ALTERED PLAYER SPRITE SHEETS
        bigPlayerStand = new TextureRegion(screen.getAtlas().findRegion("lightskull_run"),0,0, lightskullRunWidth, lightskullRunHeight);

        for(int i = 0; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"),i * lightskullRunWidth, 0, lightskullRunWidth, lightskullRunHeight));
        bigPlayerRun = new Animation(0.1f, frames);
        frames.clear();
        for(int i = 0; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("lightskull_run"), i * lightskullRunWidth, 0, lightskullRunWidth, lightskullRunHeight));
        bigPlayerJump = new Animation(0.1f, frames);
        frames.clear();

        // Define Player in box2d
        definePlayer();
        // !!! 'setBounds' is the method that determines Player size on screen
        setBounds(0, 0, 50 / LightSkull.PPM, 70 / LightSkull.PPM);
        // Set starting Sprite / Texture Region
        set(this);
    }

    public void update(float dt){
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // !!! THIS SETS THE POSITION OF THE SPRITE IMAGES ON THE BOX2D BODY CREATED !!! ////////////////////////////////////////////
        if(playerIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / LightSkull.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 + 3 / LightSkull.PPM);
        /// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ ///////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        setRegion(getFrame(dt));
        if(timeToDefineBigPlayer)
            defineBigPlayer();
        if(timeToRedefinePlayer)
            redefinePlayer();
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch(currentState){
            case DEAD:
                region = (TextureRegion) playerDead.getKeyFrame(stateTimer);
                break;
            case GROWING:
                region = (TextureRegion) growPlayer.getKeyFrame(stateTimer);
                if(growPlayer.isAnimationFinished(stateTimer))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                region = playerIsBig ? (TextureRegion) bigPlayerJump.getKeyFrame(stateTimer, true) : (TextureRegion) playerJump.getKeyFrame(stateTimer,true);
                break;
            case MOVING_RIGHT_LEFT:
                region = playerIsBig ? (TextureRegion) bigPlayerRun.getKeyFrame(stateTimer, true) : (TextureRegion) playerMoveRightLeft.getKeyFrame(stateTimer, true);
                break;
            case MOVING_UP:
                region = (TextureRegion) playerMoveUp.getKeyFrame(stateTimer,true);
                break;
            case MOVING_DOWN:
                region = (TextureRegion) playerMoveDown.getKeyFrame(stateTimer,true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = playerIsBig ? bigPlayerStand : (TextureRegion) playerStand.getKeyFrame(stateTimer,true);
                break;
        }
        if((b2body.getLinearVelocity().x < 0 || !movingRight) && !region.isFlipX()){
            region.flip(true, false);
            movingRight = false;
        }else if ((b2body.getLinearVelocity().x > 0 || movingRight) && region.isFlipX()){
            region.flip(true, false);
            movingRight = true;
        }

        // if the current state is the same as the previous state increase the state timer.
        // otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        // update previous state
        previousState = currentState;
        // return our final adjusted frame
        return region;
    }

    public State getState(){
        if(playerIsDead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(b2body.getLinearVelocity().x != 0)
            return State.MOVING_RIGHT_LEFT;
        else if(movingUp)
            return State.MOVING_UP;
        else if(movingDown)
            return State.MOVING_DOWN;
        else
            return State.STANDING;
    }

    public void grow(){
        runGrowAnimation = true;
        playerIsBig = true;
        timeToDefineBigPlayer = true;
        setBounds(getX(),getY()/ LightSkull.PPM,getWidth(),getHeight() + 10 / LightSkull.PPM);
        LightSkull.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void hit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.INJURED_STANDING) {
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }
        else if (playerIsBig) {
                playerIsBig = false;
                timeToRedefinePlayer = true;
                setBounds(getX(), getY(), getWidth(), getHeight() - 10 / LightSkull.PPM);
                LightSkull.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            }
        else {
                // FIX THIS !!!!!!!!!!! FOR SOME REASON CAN'T 'STOP' MUSIC... CAUSES CRASH
//            LightSkull.manager.get("audio/music/fluffing.mp3", Sound.class).stop();
                LightSkull.manager.get("audio/sounds/death.mp3", Sound.class).play();
                playerIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = LightSkull.NOTHING_BIT;
                for (Fixture fixture : b2body.getFixtureList())
                    fixture.setFilterData(filter);
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            }

    }

    public boolean isDead(){
        return playerIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public void definePlayer(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(100 / LightSkull.PPM, 333 / LightSkull.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        // REPLACED THIS ↓↓↓
//        CircleShape shape = new CircleShape();
//        shape.setRadius(15 / LightSkull.PPM);

        // WITH THIS ↓↓↓
        PolygonShape shape = new PolygonShape();
        //////////////////////////////////////////
        fdef.filter.categoryBits = LightSkull.PLAYER_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.COIN_BIT | LightSkull.BRICK_BIT | LightSkull.ENEMY_BIT | LightSkull.OBJECT_BIT | LightSkull.ENEMY_HEAD_BIT | LightSkull.ITEM_BIT;

        fdef.shape = shape;
        shape.setAsBox(20 / LightSkull.PPM, 30 / LightSkull.PPM);
        b2body.createFixture(fdef).setUserData(this);


        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / LightSkull.PPM, 20 / LightSkull.PPM), new Vector2(2 / LightSkull.PPM, 17 / LightSkull.PPM));
        fdef.filter.categoryBits = LightSkull.PLAYER_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public void defineBigPlayer(){
        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0,1 / LightSkull.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(17 / LightSkull.PPM);
        fdef.filter.categoryBits = LightSkull.PLAYER_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.COIN_BIT | LightSkull.BRICK_BIT | LightSkull.ENEMY_BIT | LightSkull.OBJECT_BIT | LightSkull.ENEMY_HEAD_BIT | LightSkull.ITEM_BIT;

        fdef.shape = shape;
        shape.setPosition(new Vector2(0,-7 / LightSkull.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / LightSkull.PPM, 15 / LightSkull.PPM), new Vector2(2 / LightSkull.PPM, 15 / LightSkull.PPM));
        fdef.filter.categoryBits = LightSkull.PLAYER_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigPlayer = false;
    }
    public boolean isBig(){
        return playerIsBig;
    }

    public void redefinePlayer(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);
        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        // REPLACED THIS ↓↓↓
//        CircleShape shape = new CircleShape();
//        shape.setRadius(15 / LightSkull.PPM);

        // WITH THIS ↓↓↓
        PolygonShape shape = new PolygonShape();
        //////////////////////////////////////////
        fdef.filter.categoryBits = LightSkull.PLAYER_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.COIN_BIT | LightSkull.BRICK_BIT | LightSkull.ENEMY_BIT | LightSkull.OBJECT_BIT | LightSkull.ENEMY_HEAD_BIT | LightSkull.ITEM_BIT;

        fdef.shape = shape;
        shape.setAsBox(4 / LightSkull.PPM, 13 / LightSkull.PPM);
        b2body.createFixture(fdef).setUserData(this);


        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / LightSkull.PPM, 13 / LightSkull.PPM), new Vector2(2 / LightSkull.PPM, 13 / LightSkull.PPM));
        fdef.filter.categoryBits = LightSkull.PLAYER_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefinePlayer = false;
    }
}
