package com.gameoff.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gameoff.game.managers.LevelManager;
import com.gameoff.game.managers.TitleMenuManager;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.input.InputDefaults;
import com.kyperbox.input.KeyboardMapping;

public class GameOffGame extends KyperBoxGame {

	public static final int WIDTH = (int) (1280*.75f);
	public static final int HEIGHT = (int) (720*.75f);

	public GameOffGame() {
		super(new FitViewport(WIDTH, HEIGHT));
		
	}

	@Override
	public void initiate() {
		// TODO Auto-generated method stubs
		DEBUG_LOGGING = true;
		
		ObjectFactory.createObjectGetters(this);
		
		//register game states (tmx maps with the template setup)
		registerGameState("title", "testmenu.tmx",new TitleMenuManager());
		registerGameState("level", "testlevel.tmx", new LevelManager());
		
		setGameState("title");
		
		//setup input
		getInput().addInputMapping(InputDefaults.MOVE_UP, new KeyboardMapping(Keys.UP));
		getInput().addInputMapping(InputDefaults.MOVE_DOWN, new KeyboardMapping(Keys.DOWN));
		getInput().addInputMapping(InputDefaults.MOVE_LEFT, new KeyboardMapping(Keys.LEFT));
		getInput().addInputMapping(InputDefaults.MOVE_RIGHT, new KeyboardMapping(Keys.RIGHT));
		getInput().addInputMapping(InputDefaults.JUMP_BUTTON, new KeyboardMapping(Keys.SPACE));
		getInput().addInputMapping(InputDefaults.ACTION_BUTTON, new KeyboardMapping(Keys.F));
	}
	
}
