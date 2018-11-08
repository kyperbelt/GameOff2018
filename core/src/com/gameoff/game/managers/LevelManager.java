package com.gameoff.game.managers;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.gameoff.game.GameLevel;
import com.gameoff.game.Room;
import com.gameoff.game.objects.*;
import com.kyperbox.systems.LayerSystem;
import com.gameoff.game.systems.DeathSystem;
import com.gameoff.game.systems.MoveSystem;
import com.gameoff.game.systems.OutOfBoundsSystem;
import com.gameoff.game.systems.PlayerCameraSystem;
import com.gameoff.game.systems.PlayerControlSystem;
import com.gameoff.game.systems.YSortSystem;
import com.kyperbox.GameState;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.managers.Priority;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.GameLayer;
import com.kyperbox.objects.GameObject;
import com.kyperbox.systems.ParallaxMapper;
import com.kyperbox.systems.QuadTree;
import com.badlogic.gdx.utils.Array;

public class LevelManager extends StateManager {

	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;

	private int entryPoint;

	// test player for now
	Player player;

	// layer systems we will use for playground layer
	QuadTree quad;// collisionSystem
	MoveSystem move;
	PlayerControlSystem control;
	PlayerCameraSystem camera;
	DeathSystem death;
	YSortSystem ysort;
	ParallaxMapper paralax;
	OutOfBoundsSystem bounds;
	int m_roomWidthPixels, m_roomHeightPixels;

	/**
	 * set the entry point into new room
	 * <p>
	 * North=0,East=1, South=2,West=3
	 * 
	 * @param entryPoint
	 */
	public void setEntryPoint(int entryPoint) {
		this.entryPoint = entryPoint;
	}

	public LevelManager() {
		setEntryPoint(NORTH);
	}

	@Override
	public void addLayerSystems(GameState state) {

		System.out.println("LevelManager::addLayerSystems called: State=" + state.getName());
		Player.createPlayerAnimations(state);
		// get the playground layer from the state
		GameLayer playground = state.getPlaygroundLayer();

		// get the map data and a tile layer to set the world size of the level.
		TiledMapTileLayer floor = (TiledMapTileLayer) state.getMapData().getLayers().get("floor_tiles");
		m_roomWidthPixels = (int) (floor.getWidth() * floor.getTileWidth());
		m_roomHeightPixels = (int) (floor.getHeight() * floor.getTileHeight());

		// set the size of this quad tree. Anything outside the bounds will not have
		// collision detection
		quad = new QuadTree(m_roomWidthPixels, m_roomHeightPixels);

		move = new MoveSystem();
		control = new PlayerControlSystem();

		// the camera has bounds that it will not move past
		camera = new PlayerCameraSystem(m_roomWidthPixels, m_roomHeightPixels);

		death = new DeathSystem();

		ysort = new YSortSystem();

		// ssystem to detect when a player is out of bounds and then transition room
		bounds = new OutOfBoundsSystem(0, 0, m_roomWidthPixels, m_roomHeightPixels);
		bounds.setPriority(Priority.LOW);

		// add all the systems to the playground layer. If we want things like collision
		// on a separate layer
		// then we must add systems(unique) to that layer as well.
		playground.addLayerSystem(control);
		playground.addLayerSystem(quad);
		playground.addLayerSystem(move);
		playground.addLayerSystem(camera);
		playground.addLayerSystem(death);
		playground.addLayerSystem(ysort);
		playground.addLayerSystem(bounds);

		// background
		GameLayer background = state.getBackgroundLayer();
		background.getCamera().setCentered();// center the camera of the background layer so it lines up with the
												// playground layer

		// create a parallax mapper and set its camera layer as the playground layer
		paralax = new ParallaxMapper(playground);

		// add a mapping for the Back object and set its scale 1,1 so that there is no
		// scroll delay
		paralax.addMapping("Back", 1f, 1f, true);

		// ad the mapper to the background layer
		background.addLayerSystem(paralax);
	}

	@Override
	public void dispose(GameState state) {
		// reset all projectiles & melee attack pools
		Projectile.resetProjectiles();
		MeleeAttack.resetMeleeAttacks();
	}

	@Override
	public void init(GameState state) {

		System.out.println("LevelManager::init called. State=" + state.getName());
		// When this is called, TMX is loaded, objects created, etc.
		// and this is called right before being shown.
		GameLayer playground = state.getPlaygroundLayer();

		if (player == null) {
			GameObject pspawn = playground.getGameObject("playerSpawn");

			player = new Player(0);
			player.setName("player1");

			float x = 0;
			float y = 0;

			if (pspawn != null) {
				x = pspawn.getCollisionCenter().x;
				y = pspawn.getCollisionCenter().y;
			}

			player.setPosition(x, y);

		} else {

			//Bug if we don't create new player where player no longer collides
			//with walls or pits. I tried for an hour to figure why?
			//Thought it was controllers needed to be removed and re-added...so I removed
			//all player controllers, then let them be re-added as usual in init- that 
			//didn't fix it. Finally just created new player and it works.
			//Need to fix this, things like health/state need to be preserved.
			//player = new Player();
			//player.setName("player1");


			float x = 0;
			float y = 0;
			
			switch (entryPoint) {
			case NORTH:
				GameObject north = playground.getGameObject("north");
				x = north.getX();
				y = north.getY();
				break;
			case EAST:
				GameObject east = playground.getGameObject("east");
				x = east.getX();
				y = east.getY();
				break;
			case SOUTH:
				GameObject south = playground.getGameObject("south");
				x = south.getX();
				y = south.getY();
				break;
			case WEST:
				GameObject west = playground.getGameObject("west");
				x = west.getX();
				y = west.getY();
				break;
			}
			player.setPosition(x, y);

		}

		playground.getCamera().setPosition(player.getX(), player.getY());
		playground.getCamera().update();
		player.onRemove();
		playground.addGameObject(player, KyperBoxGame.NULL_PROPERTIES);

		KyperBoxGame game = state.getGame();
		GameLevel level = GameLevel.getCurrentLevel();

		Room r = level.getCurrentRoom();
		System.out.println("LevelManager::init current room " + r.getX()+ ", " + r.getY());

		// Place Doors & Walls
		// Not sure about collision box rotation/how that all works
		// Might be smarter to play with origin for rotation, this
		// is a bit hacky ;)
		for (int dir = 0; dir < 4; dir++) {
			int dc = r.getDoor(dir);
			System.out.println("LevelManager::init door[" + dir + "]="+dc);
			int rot = -90 * dir;
			// change below to getSize of sprite somehow
			float dw = 160;;
			float dh = 32;

			float x = m_roomWidthPixels / 2 - dw / 2;
			float y = m_roomHeightPixels - dh;

			if (dc > 0) {
				Door d = new Door(dc);
				d.setName("Door" + dir);
				d.setSize(dw, dh);
				d.setRotation(rot);
				playground.addGameObject(d, null);

				if (dir == 2) {
					// down

					Wall t1 = new Wall();
					t1.setSize(x, dh);
					t1.setPosition(0, 0);
					playground.addGameObject(t1, null);

					Wall t2 = new Wall();
					t2.setSize(x, dh);
					t2.setPosition(x + dw, 0);
					playground.addGameObject(t2, null);

					// rotated 180
					y = 0;

				} else if (dir == 1) {
					// right door
					y = m_roomHeightPixels / 2 - dw / 2;
					x = m_roomWidthPixels - dh;

					Wall t1 = new Wall();
					t1.setSize(dh, y);
					t1.setPosition(x, 0);
					playground.addGameObject(t1, null);

					Wall t2 = new Wall();
					t2.setSize(dh, y);
					t2.setPosition(x, y + dw);
					playground.addGameObject(t2, null);

					// adjust for rotation
					x -= (dw/2 - dh/2);
					y += dw / 2;

				} else if (dir == 3) {
					// left door
					y = m_roomHeightPixels / 2 - dw / 2;
					x = 0;

					Wall t1 = new Wall();
					t1.setSize(dh, y);
					t1.setPosition(0, 0);
					playground.addGameObject(t1, null);

					Wall t2 = new Wall();
					t2.setSize(dh, y);
					t2.setPosition(0, y + dw);
					playground.addGameObject(t2, null);

					// rotation adjustment
					x -= (dw/2 - dh/2);
					y += dw / 2;

				} else {
					// top
					Wall t1 = new Wall();
					t1.setSize(x, dh);
					t1.setPosition(0, y);
					playground.addGameObject(t1, null);

					Wall t2 = new Wall();
					t2.setSize(x, dh);
					t2.setPosition(x + dw, y);
					playground.addGameObject(t2, null);
				}
				d.setPosition(x, y);

			} else {
				// just place full walls
				if (dir == 2) {
					Wall t1 = new Wall();
					t1.setSize(m_roomWidthPixels, dh);
					t1.setPosition(0, 0);
					playground.addGameObject(t1, null);
				} else if (dir == 1) {
					// right door
					Wall t1 = new Wall();
					t1.setSize(dh, m_roomHeightPixels);
					t1.setPosition(m_roomWidthPixels - dh, 0);
					playground.addGameObject(t1, null);

				} else if (dir == 3) {
					// left door
					Wall t1 = new Wall();
					t1.setSize(dh, m_roomHeightPixels);
					t1.setPosition(0, 0);
					playground.addGameObject(t1, null);
				} else {
					// top
					Wall t1 = new Wall();
					t1.setSize(m_roomWidthPixels, dh);
					t1.setPosition(0, m_roomHeightPixels - dh);
					playground.addGameObject(t1, null);
				}
			}
		}

		// TODO: Spawn Enemies
		// Place enemies based on TMX Spawn Objects?

		// TODO: Keys
		// Place room keys and bosses based on Room date

		GameLayer flayer = state.getForegroundLayer();
		HudMap mapHud = new HudMap(level);
		mapHud.updateLevel(level);
		mapHud.setRecommendedPosition(960, 540);
		flayer.addGameObject(mapHud, KyperBoxGame.NULL_PROPERTIES);
	}

	@Override
	public void update(GameState state, float delta) {

	}

}
