package com.gameoff.game;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gameoff.game.managers.GameOverManager;
import com.gameoff.game.managers.LevelManager;
import com.gameoff.game.managers.InstructionsManager;
import com.gameoff.game.managers.OverlayManager;
import com.gameoff.game.managers.VictoryManager;
import com.gameoff.game.managers.TitleScreenManager;
import com.gameoff.game.managers.IntroScreenManager;
import com.kyperbox.GameState;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.console.CommandRunnable;
import com.kyperbox.console.ConsoleCommand;
import com.kyperbox.console.DevConsole;
import com.kyperbox.input.ControllerMapping;
import com.kyperbox.input.ControllerMaps;
import com.kyperbox.input.ICWrapper;
import com.kyperbox.input.KeyboardMapping;
import com.kyperbox.input.KyperController;
import com.kyperbox.objects.GameLayer;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.IGameObjectFactory;
import com.kyperbox.umisc.StringUtils;

public class GameOffGame extends KyperBoxGame {

	public static final int WIDTH = (int) (1280 * .75f);
	public static final int HEIGHT = (int) (720 * .75f);

	private LevelManager levelmanager;

	public ICWrapper controller; // controller wrapper - we will only allow 1 controller for now

	// this are the controller mappings that we will create - they are just mapped
	// to a controller but
	// can also be mapped to different inputs/ this can allows to allow players to
	// rebind their controls
	public ControllerMapping interact, attack, transform, dpadleft, dpadright, dpadup, dpaddown, sleft, sright, sup,
			sdown, dash;

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
		AiNodeFactory.createNodeDic(); // create the dictionary to load nodes from file

		// register game states (tmx maps with the template setup)
		registerGameState("title", "testmenu.tmx", new TitleScreenManager());
		registerGameState("instructions", "instructions.tmx", new InstructionsManager());
		registerGameState("intro", "introduction.tmx", new IntroScreenManager());
		// registerGameState("level", "testlevel.tmx", new LevelManager());
		levelmanager = new LevelManager();

		registerGameState("room_0", "room_0.tmx", levelmanager);
		registerGameState("room_1", "room_1.tmx", levelmanager);
		registerGameState("room_2", "room_2.tmx", levelmanager);
		registerGameState("room_3", "room_3.tmx", levelmanager);
		registerGameState("room_4", "room_4.tmx", levelmanager);
		registerGameState("room_10", "room_10.tmx", levelmanager);
		registerGameState("room_20", "room_20.tmx", levelmanager);
		registerGameState("room_35", "room_35.tmx", levelmanager);
		registerGameState("room_50", "room_50.tmx", levelmanager);
		registerGameState("room_60", "room_60.tmx", levelmanager);
		registerGameState("gameOverlay", "gameOverlay.tmx", new OverlayManager());
		registerGameState("gameover", "gameover.tmx", new GameOverManager());
		registerGameState("victory", "victory.tmx", new VictoryManager());

		setGameState("intro");

		// setup input
		Inputs.registerInputs(getInput());
		getInput().addInputMapping(Inputs.UP, new KeyboardMapping(Keys.UP));
		getInput().addInputMapping(Inputs.DOWN, new KeyboardMapping(Keys.DOWN));
		getInput().addInputMapping(Inputs.LEFT, new KeyboardMapping(Keys.LEFT));
		getInput().addInputMapping(Inputs.RIGHT, new KeyboardMapping(Keys.RIGHT));
		getInput().addInputMapping(Inputs.TRANSFORM, new KeyboardMapping(Keys.SPACE));
		getInput().addInputMapping(Inputs.ATTACK, new KeyboardMapping(Keys.F));
		getInput().addInputMapping(Inputs.SPECIAL, new KeyboardMapping(Keys.G));
		getInput().addInputMapping(Inputs.ATTACK, new KeyboardMapping(Keys.ENTER));
		getInput().addInputMapping(Inputs.MENU, new KeyboardMapping(Keys.ESCAPE));

		// controller support
		controllerSupport();

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

	public void controllerSupport() {

		if (Controllers.getControllers().size > 0) {
			System.out.println("Controllers found:" + Controllers.getControllers().size);
			for (int i = 0; i < Controllers.getControllers().size; i++) {
				Controller c = Controllers.getControllers().get(i);
				if (c != null) {
					controller = new KyperController(c, GamePadMapper.getMapsForController(c));
				}
			}

			getInput().addInputMapping(Inputs.ATTACK,
					attack = new ControllerMapping(controller, ControllerMaps.BUTTON_A));
			getInput().addInputMapping(Inputs.TRANSFORM,
					transform = new ControllerMapping(controller, ControllerMaps.BUTTON_X));
			getInput().addInputMapping(Inputs.INTERACT,
					interact = new ControllerMapping(controller, ControllerMaps.BUTTON_B));

			getInput().addInputMapping(Inputs.UP, dpadup = new ControllerMapping(controller, ControllerMaps.DPAD_UP));
			getInput().addInputMapping(Inputs.DOWN,
					dpaddown = new ControllerMapping(controller, ControllerMaps.DPAD_DOWN));
			getInput().addInputMapping(Inputs.LEFT,
					dpadleft = new ControllerMapping(controller, ControllerMaps.DPAD_LEFT));
			getInput().addInputMapping(Inputs.RIGHT,
					dpadright = new ControllerMapping(controller, ControllerMaps.DPAD_RIGHT));

			getInput().addInputMapping(Inputs.UP, sup = new ControllerMapping(controller, ControllerMaps.LAXIS_UP));
			getInput().addInputMapping(Inputs.DOWN,
					sdown = new ControllerMapping(controller, ControllerMaps.LAXIS_DOWN));
			getInput().addInputMapping(Inputs.LEFT,
					sleft = new ControllerMapping(controller, ControllerMaps.LAXIS_LEFT));
			getInput().addInputMapping(Inputs.RIGHT,
					sright = new ControllerMapping(controller, ControllerMaps.LAXIS_RIGHT));

		}

		Controllers.addListener(new ControllerListener() {

			@Override
			public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean povMoved(Controller controller, int povCode, PovDirection value) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void disconnected(Controller controller) {
				log("controllers", StringUtils.format("%s disconnected", controller.getName()));
				if (GameOffGame.this.controller != null) {
					if (GameOffGame.this.controller.getController() == controller) {
						GameOffGame.this.controller.remove();
						GameOffGame.this.controller = null;

						attack.removeMapping();
						transform.removeMapping();
						interact.removeMapping();

						dpadup.removeMapping();
						dpaddown.removeMapping();
						dpadleft.removeMapping();
						dpadright.removeMapping();

						attack = null;
						transform = null;
						interact = null;

						dpadup = null;
						dpaddown = null;
						dpadleft = null;
						dpadright = null;

						sup = null;
						sdown = null;
						sleft = null;
						sright = null;
					}

				}

			}

			@Override
			public void connected(Controller controller) {
				log("controllers", StringUtils.format("%s connected", controller.getName()));

				if (GameOffGame.this.controller == null) {
					GameOffGame.this.controller = new KyperController(controller,
							GamePadMapper.getMapsForController(controller));

					getInput().addInputMapping(Inputs.ATTACK,
							attack = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.BUTTON_A));
					getInput().addInputMapping(Inputs.TRANSFORM,
							transform = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.BUTTON_X));
					getInput().addInputMapping(Inputs.INTERACT,
							interact = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.BUTTON_B));

					getInput().addInputMapping(Inputs.UP,
							dpadup = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.DPAD_UP));
					getInput().addInputMapping(Inputs.DOWN,
							dpaddown = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.DPAD_DOWN));
					getInput().addInputMapping(Inputs.LEFT,
							dpadleft = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.DPAD_LEFT));
					getInput().addInputMapping(Inputs.RIGHT,
							dpadright = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.DPAD_RIGHT));

					getInput().addInputMapping(Inputs.UP,
							sup = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.LAXIS_UP));
					getInput().addInputMapping(Inputs.DOWN,
							sdown = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.LAXIS_DOWN));
					getInput().addInputMapping(Inputs.LEFT,
							sleft = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.LAXIS_LEFT));
					getInput().addInputMapping(Inputs.RIGHT,
							sright = new ControllerMapping(GameOffGame.this.controller, ControllerMaps.LAXIS_RIGHT));
				}

			}

			@Override
			public boolean buttonUp(Controller controller, int buttonCode) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean buttonDown(Controller controller, int buttonCode) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean axisMoved(Controller controller, int axisCode, float value) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

}
