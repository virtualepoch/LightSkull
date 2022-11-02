package com.virtualepoch.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.awt.Dimension;
import java.awt.Toolkit;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {

	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static double screenSizeWidth = screenSize.getWidth();
	static double screenSizeHeight = screenSize.getHeight();
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("LightSkull");
//		config.setWindowedMode((int) screenSizeWidth, (int) screenSizeHeight);
		config.setWindowedMode(1800, 900);

		new Lwjgl3Application(new LightSkull(), config);
	}
}
