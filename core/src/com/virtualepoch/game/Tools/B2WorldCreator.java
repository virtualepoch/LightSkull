package com.virtualepoch.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Brick;
import com.virtualepoch.game.Sprites.Coin;
import com.virtualepoch.game.Sprites.Enemies.Enemy;
import com.virtualepoch.game.Sprites.Enemies.Goomba;
import com.virtualepoch.game.Sprites.Enemies.Turtle;

public class B2WorldCreator {
    private Array<Goomba> goombas;
    private Array<Turtle> turtles;

    public B2WorldCreator(PlayScreen screen){
        World world = screen.getWorld();
        TiledMap map = screen.getMap();

        //create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //create ground bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / LightSkull.PPM, (rect.getY() + rect.getHeight() / 2) / LightSkull.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / LightSkull.PPM, rect.getHeight() / 2 / LightSkull.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }
        //create pipe bodies/fixtures
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / LightSkull.PPM, (rect.getY() + rect.getHeight() / 2) / LightSkull.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / LightSkull.PPM, rect.getHeight() / 2 / LightSkull.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = LightSkull.OBJECT_BIT;
            body.createFixture(fdef);
        }
        //create brick bodies/fixtures
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen, object);
        }
        //create coin bodies/fixtures
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            new Coin(screen, object);
        }
        //create all goombas
        goombas = new Array<Goomba>();
            for(MapObject object : map.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                goombas.add(new Goomba(screen,rect.getX()/ LightSkull.PPM, rect.getY() / LightSkull.PPM));
        }
        turtles = new Array<Turtle>();
        for(MapObject object : map.getLayers().get(9).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen,rect.getX()/ LightSkull.PPM, rect.getY() / LightSkull.PPM));
        }
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }

}

