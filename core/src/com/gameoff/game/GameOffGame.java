package com.gameoff.game;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gameoff.game.managers.LevelManager;
import com.gameoff.game.managers.TitleMenuManager;
import com.kyperbox.KyperBoxGame;

public class GameOffGame extends KyperBoxGame {

	public static final int WIDTH = (int) (1280);
	public static final int HEIGHT = (int) (720);

	public GameOffGame() {
		super(new FitViewport(WIDTH, HEIGHT));
		
	}

	@Override
	public void initiate() {
		// TODO Auto-generated method stubs
		DEBUG_LOGGING = true;
		

		registerGameState("title", "testmenu.tmx",new TitleMenuManager());
		registerGameState("level", "testlevel.tmx", new LevelManager());
		
		setGameState("title");

	}
	
}
