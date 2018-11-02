package com.gameoff.game.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.gameoff.game.GameOffGame;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.Resolutions;
import com.kyperbox.console.CommandRunnable;
import com.kyperbox.console.ConsoleCommand;
import com.kyperbox.console.DevConsole;

public class DesktopLauncher {
	
	
	public static final boolean VSYNC = true;
	public static final boolean CONSOLE = true;
	public static final boolean FULLSCREEN = false;
	public static final boolean DECORATED = true;
	
	public static void main (String[] arg) {
		
		GameOffGame game = new GameOffGame();
		

		DevConsole console = new DevConsole("console.fnt", "shade1.png", Keys.GRAVE);
		if (CONSOLE) {
			game.setDevConsole(console);
		}

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//
		config.setTitle(game.getGameName());
		if (FULLSCREEN)
			config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		else
			config.setWindowedMode(GameOffGame.WIDTH, GameOffGame.HEIGHT);
		config.useVsync(VSYNC);
		config.setDecorated(DECORATED);
		
		//register dev console exclusives
		
		console.addCommand(new ConsoleCommand("fullscreen", 0, "toggle fullscreen", new CommandRunnable() {
			
			int last_width = GameOffGame.WIDTH;
			int last_height = GameOffGame.HEIGHT;
			
			@Override
			public boolean executeCommand(DevConsole console, String... args) {
				if(Gdx.graphics.isFullscreen()) {
					Gdx.graphics.setWindowedMode(last_width, last_height);
					Gdx.graphics.setVSync(VSYNC);
				}else {
					Gdx.graphics.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
					Gdx.graphics.setVSync(VSYNC);
				}
				return true;
			}
		}));
		new Lwjgl3Application(game, config);
	}
}
