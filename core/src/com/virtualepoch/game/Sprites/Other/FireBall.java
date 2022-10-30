package com.virtualepoch.game.Sprites.Other;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Player;

public class FireBall extends Projectile {

    public FireBall(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0,16,16);
        velocity = new Vector2(0.7f,-1);
    }

    @Override
    public void defineProjectile() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(player.getX(), player.getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / LightSkull.PPM);
        fdef.filter.categoryBits = LightSkull.ITEM_BIT;
        fdef.filter.maskBits = LightSkull.PLAYER_BIT | LightSkull.OBJECT_BIT | LightSkull.GROUND_BIT | LightSkull.COIN_BIT | LightSkull.BRICK_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void fire() {

        destroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        body.setLinearVelocity(velocity);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}

