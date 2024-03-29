package com.kandclay.drop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.kandclay.drop.Drop;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(800,480);
		config.setForegroundFPS(60);
		config.setTitle("Drop");
		new Lwjgl3Application(new Drop(), config);
	}
}
