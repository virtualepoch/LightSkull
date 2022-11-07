package com.virtualepoch.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.virtualepoch.game.Screens.PlayScreen;

public class LightSkull extends Game {
	public static final int V_WIDTH = 600;
	public static final int V_HEIGHT = 300;
	//create PPM (PIXELS PER METER) variable
	public static final float PPM = 100;

	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short OBJECT_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short PLAYER_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short PROJECTILE_BIT = 128;
	public static final short PLAYER_HEAD_BIT = 256;
	public static final short ENEMY_HEAD_BIT = 512;

	public static SpriteBatch batch;

//	WARNING Using AssetManager in a static way can cause issues, especially on Android. Instead you may want to pass around AssetManager to those classes that need it

	public static AssetManager manager;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/lv1_1.mp3", Music.class);
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/breakblock.wav", Sound.class);
		manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
		manager.load("audio/sounds/powerup.wav", Sound.class);
		manager.load("audio/sounds/powerdown.wav", Sound.class);
		manager.load("audio/sounds/stomp.wav", Sound.class);
		manager.load("audio/sounds/death.mp3", Sound.class);
		manager.finishLoading();

		setScreen(new PlayScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		manager.dispose();
		batch.dispose();
	}

	@Override
	public void render () {
		super.render();
	}
}
