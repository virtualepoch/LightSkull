package com.virtualepoch.game.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Sprites.Enemies.Enemy;
import com.virtualepoch.game.Sprites.Projectiles.Projectile;
import com.virtualepoch.game.Sprites.Player;
import com.virtualepoch.game.Sprites.TileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case LightSkull.PLAYER_HEAD_BIT | LightSkull.BRICK_BIT:
            case LightSkull.PLAYER_HEAD_BIT | LightSkull.COIN_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.PLAYER_HEAD_BIT)
                    ((InteractiveTileObject)fixB.getUserData()).onHeadHit((Player) fixA.getUserData());
                else
                    ((InteractiveTileObject)fixA.getUserData()).onHeadHit((Player) fixB.getUserData());
                break;

            case LightSkull.ENEMY_HEAD_BIT | LightSkull.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.ENEMY_HEAD_BIT)
                    ((Enemy)fixA.getUserData()).hitByLaser((Player) fixB.getUserData());
                else
                    ((Enemy)fixB.getUserData()).hitByLaser((Player) fixA.getUserData());
                break;

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
                if(fixA.getFilterData().categoryBits == LightSkull.PROJECTILE_BIT)
                    ((Projectile)fixA.getUserData()).hitEnemy((Enemy)fixB.getUserData());
                else
                    ((Projectile)fixB.getUserData()).hitEnemy((Enemy)fixA.getUserData());
                break;

            case LightSkull.PROJECTILE_BIT | LightSkull.GROUND_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.PROJECTILE_BIT)
                    ((Projectile)fixA.getUserData()).hitObject();
                else if(!(fixA.getUserData()instanceof String))
                    ((Projectile)fixB.getUserData()).hitObject();
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
