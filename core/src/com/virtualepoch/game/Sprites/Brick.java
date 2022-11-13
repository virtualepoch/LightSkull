package com.virtualepoch.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.virtualepoch.game.LightSkull;
import com.virtualepoch.game.Scenes.Hud;
import com.virtualepoch.game.Screens.PlayScreen;
import com.virtualepoch.game.Sprites.TileObjects.InteractiveTileObject;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(LightSkull.BRICK_BIT);
    }

//    @Override
//    public void onHeadHit(Player player) {
//        if(player.isBig()) {
//            setCategoryFilter(LightSkull.DESTROYED_BIT);
//            getCell().setTile(null);
//            Hud.addScore(200);
//            LightSkull.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
//        }
//        LightSkull.manager.get("audio/sounds/bump.wav", Sound.class).play();
//        Hud.addScore(200);
//    }
}
