package com.virtualepoch.game.Sprites.Projectiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Enemies.Enemy;

public class SmallLaser extends Projectile {

    private float stateTime;
    private Animation<TextureRegion> animation;

    public SmallLaser(PlayScreen screen, float x, float y) {
        super(screen, x, y);
//        currentState = State.IDLE;
//        previousState = State.IDLE;

        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 8; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i * 41, 0, 41, 32));
        animation = new Animation(0.05f, frames);
        frames.clear();

        stateTime = 0;
        velocity = new Vector2(5f, 0);
    }

    @Override
    public void defineProjectile() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5 / LightSkull.PPM);
        fdef.filter.categoryBits = LightSkull.PROJECTILE_BIT;
        fdef.filter.maskBits = LightSkull.GROUND_BIT | LightSkull.ENEMY_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
        setBounds(getX(), getY(), 60 / LightSkull.PPM, 10 / LightSkull.PPM);
    }

    public TextureRegion getFrame(float dt){
//        currentState = getState();
//
//        TextureRegion region;
//        switch(currentState){
//            case MOVING_RIGHT_LEFT:
              TextureRegion region = (TextureRegion) animation.getKeyFrame(stateTime, true);
//                break;
//            default:
//                region = null;
//                break;
//        }
        if((body.getLinearVelocity().x < 0 || !movingRight) && !region.isFlipX()){
            region.flip(true, false);
            movingRight = false;
        }else if ((body.getLinearVelocity().x > 0 || movingRight) && region.isFlipX()){
            region.flip(true, false);
            movingRight = true;
        }
        // if the current state is the same as the previous state increase the state timer.
        // otherwise the state has changed and we need to reset timer.
//        stateTime = currentState == previousState ? stateTime + dt : 0;
        // update previous state
//        previousState = currentState;
        // return our final adjusted frame
        return region;
    }

    @Override
    public void hitEnemy(Enemy enemy) {
        enemy.hitByLaser(player);
        destroy();
    }

    @Override
    public void hitObject() {
        destroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        stateTime +=dt;
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
            stateTime = 0;
        }
        else if (!destroyed){
            body.setLinearVelocity(velocity);
//            velocity.y = body.getLinearVelocity().y;
            // THIS IS WHERE YOU CAN CHANGE THE SPRITE IMAGE POSITION ON THE OBJECTS B2BODY //
            if(velocity.x < 0)
                setPosition(body.getPosition().x - getWidth() / 5f, body.getPosition().y - getHeight() / 2);
            else
                setPosition(body.getPosition().x - getWidth() / 1.3f, body.getPosition().y - getHeight() / 2);
            setRegion(getFrame(dt));
        }
    }
}
