package com.virtualepoch.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Sprites.Enemies.Enemy;
import com.virtualepoch.game.Sprites.Items.Item;
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
                    ((Enemy)fixA.getUserData()).hitOnHead();
                else
                    ((Enemy)fixB.getUserData()).hitOnHead();
                break;

            case LightSkull.ENEMY_BIT | LightSkull.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case LightSkull.PLAYER_BIT | LightSkull.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.PLAYER_BIT)
                    ((Player) fixA.getUserData()).hit();
                else
                    ((Player) fixB.getUserData()).hit();
                break;

            case LightSkull.ENEMY_BIT | LightSkull.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case LightSkull.ITEM_BIT | LightSkull.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;

            case LightSkull.ITEM_BIT | LightSkull.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == LightSkull.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Player) fixB.getUserData());
                else if(!(fixA.getUserData()instanceof String))
                    ((Item)fixB.getUserData()).use((Player) fixA.getUserData());
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
