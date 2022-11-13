package com.virtualepoch.game.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Scenes.Hud;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.Projectiles.ProjectileDef;
import com.virtualepoch.game.Sprites.Projectiles.SmallLaser;
import com.virtualepoch.game.Sprites.TileObjects.InteractiveTileObject;

public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 6923;

    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("level1_1_tileset");
        fixture.setUserData(this);
        setCategoryFilter(LightSkull.COIN_BIT);
    }

//    @Override
//    public void onHeadHit(Player player) {
//        if(getCell().getTile().getId() == BLANK_COIN)
//            LightSkull.manager.get("audio/sounds/bump.wav", Sound.class).play();
//        else {
//            if(object.getProperties().containsKey("mushroom")){
//                screen.spawnProjectile(new ProjectileDef(new Vector2(body.getPosition().x, body.getPosition().y + 16/ LightSkull.PPM), SmallLaser.class));
//                LightSkull.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play(0.5f);
//            }
//            else
//                LightSkull.manager.get("audio/sounds/coin.wav", Sound.class).play();
//        }
//        getCell().setTile(tileSet.getTile(BLANK_COIN));
//        Hud.addScore(100);
//    }
}
