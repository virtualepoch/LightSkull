package com.virtualepoch.game.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Sprites.Enemies.Enemy;
import com.virtualepoch.game.Sprites.Bullet;
import com.virtualepoch.game.Sprites.Player;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case LightSkull.ENEMY_BIT | LightSkull.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case LightSkull.PLAYER_BIT | LightSkull.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.PLAYER_BIT)
                    ((Player) fixA.getUserData()).hit((Enemy)fixB.getUserData());
                else
                    ((Player) fixB.getUserData()).hit((Enemy)fixA.getUserData());
                break;

            case LightSkull.ENEMY_BIT | LightSkull.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).onEnemyHit((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).onEnemyHit((Enemy)fixA.getUserData());
                break;

            case LightSkull.PROJECTILE_BIT | LightSkull.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.PROJECTILE_BIT) {
                    ((Bullet) fixA.getUserData()).destroy();
                    ((Enemy) fixB.getUserData()).destroy();
                }else
                    ((Bullet)fixB.getUserData()).destroy();
                    ((Enemy)fixA.getUserData()).destroy();
                break;

            case LightSkull.PROJECTILE_BIT | LightSkull.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.PROJECTILE_BIT)
                    ((Bullet)fixA.getUserData()).destroy();
                else
                    ((Bullet)fixB.getUserData()).destroy();
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
