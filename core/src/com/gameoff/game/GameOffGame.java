package com.gameoff.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gameoff.game.managers.LevelManager;
import com.gameoff.game.managers.OverlayManager;
import com.gameoff.game.managers.TitleMenuManager;
import com.kyperbox.GameState;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.console.CommandRunnable;
import com.kyperbox.console.ConsoleCommand;
import com.kyperbox.console.DevConsole;
import com.kyperbox.input.InputDefaults;
import com.kyperbox.input.KeyboardMapping;
import com.kyperbox.objects.GameLayer;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.IGameObjectFactory;
import com.kyperbox.umisc.StringUtils;

public class GameOffGame extends KyperBoxGame {

	public static final int WIDTH = (int) (1280 * .75f);
	public static final int HEIGHT = (int) (720 * .75f);

	private LevelManager levelmanager;

	public GameOffGame() {
		super(new FitViewport(WIDTH, HEIGHT));

	}
	
	public IGameObjectFactory getFactory() {
		return getObjectFactory();
	}

	/**
	 * utility method to get the level manager. I think we will be using the same
	 * level manager for all the rooms anyways.
	 * 
	 * @return
	 */
	public LevelManager getLevelManager() {
		return levelmanager;
	}

	@Override
	public void initiate() {
		DEBUG_LOGGING = false;

		ObjectFactory.createObjectGetters(this);
		AiNodeFactory.createNodeDic(); //create the dictionary to load nodes from file

		// register game states (tmx maps with the template setup)
		registerGameState("title", "testmenu.tmx", new TitleMenuManager());
		// registerGameState("level", "testlevel.tmx", new LevelManager());
		levelmanager = new LevelManager();

		registerGameState("room_0", "room_0.tmx", levelmanager);
		registerGameState("room_1", "room_1.tmx", levelmanager);
		registerGameState("gameOverlay","gameOverlay.tmx",new OverlayManager());

		setGameState("title");

		// setup input
		getInput().addInputMapping(InputDefaults.MOVE_UP, new KeyboardMapping(Keys.UP));
		getInput().addInputMapping(InputDefaults.MOVE_DOWN, new KeyboardMapping(Keys.DOWN));
		getInput().addInputMapping(InputDefaults.MOVE_LEFT, new KeyboardMapping(Keys.LEFT));
		getInput().addInputMapping(InputDefaults.MOVE_RIGHT, new KeyboardMapping(Keys.RIGHT));
		getInput().addInputMapping(InputDefaults.JUMP_BUTTON, new KeyboardMapping(Keys.SPACE));
		getInput().addInputMapping(InputDefaults.ACTION_BUTTON, new KeyboardMapping(Keys.F));

		// console commands
		getDevConsole().addCommand(new ConsoleCommand("spawn", 2,
				"command used to spawn objects on the playground layer. It takes in the name of the object to spawn and the name of the object to spawn relative to.",
				new CommandRunnable() {

					@Override
					public boolean executeCommand(DevConsole console, String... args) {

						GameState currentRoom = null;

						for (int i = 0; i < getCurrentStates().size; i++) {
							GameState s = getCurrentStates().get(i);
							if (s.getName().contains("room"))
								currentRoom = s;
						}

						if (currentRoom == null) {
							console.error("could not locate room state.");
							return false;
						}

						String spawnName = args[0].trim();
						String refName = args[1].trim();

						GameObject o = getObjectFactory().getGameObject(spawnName);

						if (o == null) {
							console.error(StringUtils.format(
									"[%s] was not a valid gameObject - unable to spawn. Make sure it was added to the objectFactory",
									spawnName));
							return false;
						}

						GameLayer playground = currentRoom.getPlaygroundLayer();

						float x = 0;
						float y = 0;

						GameObject ref = currentRoom.getPlaygroundLayer().getGameObject(refName);

						if (ref == null) {
							console.error(
									StringUtils.format("[%s] was not a valid object in playground layer.", refName));
						} else {
							x = ref.getX();
							y = ref.getY();
						}
						
						o.setPosition(x, y);
						
						playground.addGameObject(o, NULL_PROPERTIES);

						return true;
					}
				}));
	}

}
