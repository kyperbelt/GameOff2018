package com.gameoff.game.managers;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.gameoff.game.GameLevel;
import com.gameoff.game.Room;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.ZOrderControl;
import com.gameoff.game.objects.BackgroundObject;
import com.gameoff.game.objects.Collectible;
import com.gameoff.game.objects.Door;
import com.gameoff.game.objects.ForegroundObject;
import com.gameoff.game.objects.HealthBauble;
import com.gameoff.game.objects.HudMap;
import com.gameoff.game.objects.MeleeAttack;
import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.Projectile;
import com.gameoff.game.objects.Wall;
import com.gameoff.game.objects.enemies.ScorpionEnemy;
import com.gameoff.game.objects.enemies.WormEnemy;
import com.gameoff.game.objects.enemies.CherubEnemy;
import com.gameoff.game.objects.enemies.SpiderBossEnemy;
import com.gameoff.game.systems.AiSystem;
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
import com.kyperbox.objects.TilemapLayerObject;
import com.kyperbox.systems.ParallaxMapper;
import com.kyperbox.systems.QuadTree;

public class LevelManager extends StateManager {

	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;

	private Random m_random = new Random();

	private int entryPoint;

	// test player for now
	Player player;
	int overlayKeyAmount; //amount of keys that are shown to the overlay

	// layer systems we will use for playground layer
	QuadTree quad;// collisionSystem
	MoveSystem move;
	PlayerControlSystem control;
	PlayerCameraSystem camera;
	DeathSystem death;
	YSortSystem ysort;
	ParallaxMapper paralax;
	OutOfBoundsSystem bounds;
	AiSystem ai;
	
	public int m_roomWidthPixels, m_roomHeightPixels;
	
	OverlayManager overlayManager;
	boolean died = false;

	/**
	 * set the entry point into new room
	 * <p>
	 * North=0,East=1, South=2,West=3
	 * 
	 * @param entryPoint
	 */
	public void setEntryPoint(int entryPoint) {
		this.entryPoint = entryPoint;
		ai = new AiSystem();
	}

	public LevelManager() {
		setEntryPoint(NORTH);
	}

	public void randomPlaceObject(GameObject o, GameLayer layer, GameLevel level)
	{
		o.setPosition(m_roomWidthPixels/2, m_roomHeightPixels/2);
		//while (true)
		//{
		//	float w = o.getWidth();
		//	float h = o.getHeight();
			//int x = level.nextInt(m_roomWidthPixels - )
		//}
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

		// TODO: Re-use these systems so that we dont make new ones each room transition

		// --

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

		ai.onRemove();

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
		playground.addLayerSystem(ai);

		// background
		GameLayer background = state.getBackgroundLayer();
		background.getCamera().setCentered();// center the camera of the background layer so it lines up with the
												// playground layer

		// create a parallax mapper and set its camera layer as the playground layer
		paralax = new ParallaxMapper(playground);


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

		
		died = false;
		
		System.out.println("LevelManager::init called. State=" + state.getName());
		// When this is called, TMX is loaded, objects created, etc.
		// and this is called right before being shown.
		GameLayer playground = state.getPlaygroundLayer();
		
		TilemapLayerObject floor_tiles = (TilemapLayerObject) playground.getGameObject("floor_tiles");
		
		//add ZOrderControl to the TileLayerObjects
		ZOrderControl floorZOrder = new ZOrderControl();
		floorZOrder.setZOrder(ZOrder.BACKGROUND); //really high number so its always drawn on the bottom
		floor_tiles.addController(floorZOrder);
		TilemapLayerObject wall_tiles = (TilemapLayerObject) playground.getGameObject("wall_tiles");
		ZOrderControl wallZOrder = new ZOrderControl();
		wallZOrder.setZOrder(ZOrder.PLAYER+1);
		wall_tiles.addController(wallZOrder);
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

			// Bug if we don't create new player where player no longer collides
			// with walls or pits. I tried for an hour to figure why?
			// Thought it was controllers needed to be removed and re-added...so I removed
			// all player controllers, then let them be re-added as usual in init- that
			// didn't fix it. Finally just created new player and it works.
			// Need to fix this, things like health/state need to be preserved.
			// player = new Player();
			// player.setName("player1");

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
		System.out.println("LevelManager::init current room " + r.getX() + ", " + r.getY());

		r.roomWidthPixels = m_roomWidthPixels;
		r.roomHeightPixels = m_roomHeightPixels;

		//GameLayer background = state.getBackgroundLayer();
		float wallSize = 224;
		//add wall images
		BackgroundObject w1 = new BackgroundObject();
		ZOrderControl zorder = new ZOrderControl();
		zorder.setZOrder(ZOrder.BACKGROUND);
		w1.setSize(m_roomWidthPixels,wallSize);
		w1.setSprite("top_wall");
		w1.setName("TopWall");
		w1.setPosition(0,m_roomHeightPixels- wallSize);
		w1.addController(zorder);
		//background.addGameObject(w1,null);
		//paralax.addMapping("TopWall", 1f, 1f, true);
		playground.addGameObject(w1,null);

		w1 = new BackgroundObject();
		w1.setSize(m_roomWidthPixels,wallSize);
		w1.setSprite("top_wall");
		w1.setName("BottomWall");
		w1.setFlip(false, true);
		w1.setPosition(0,0);
		w1.addController(zorder);
		//background.addGameObject(w1,null);
		//paralax.addMapping("BottomWall", 1f, 1f, true);
		playground.addGameObject(w1,null);

		w1 = new BackgroundObject();
		w1.setSize(wallSize,m_roomHeightPixels - (wallSize*2));
		w1.setSprite("left_wall");
		w1.setName("LeftWall");
		w1.setPosition(0,wallSize);
		w1.addController(zorder);
		//background.addGameObject(w1,null);
		//paralax.addMapping("LeftWall", 1f, 1f, true);
		playground.addGameObject(w1,null);

		w1 = new BackgroundObject();
		w1.setSize(wallSize,m_roomHeightPixels - (wallSize*2));
		w1.setSprite("left_wall");
		w1.setName("RightWall");
		w1.setPosition(m_roomWidthPixels-wallSize,wallSize);
		w1.setFlip(true,false);
		w1.addController(zorder);
		//background.addGameObject(w1,null);
		//paralax.addMapping("RightWall", 1f, 1f, true);
		playground.addGameObject(w1,null);

		// Place Doors & Walls
		// Not sure about collision box rotation/how that all works
		// Might be smarter to play with origin for rotation, this
		// is a bit hacky ;)
		for (int dir = 0; dir < 4; dir++) {

		
			int dc = r.getDoor(dir);
			System.out.println("LevelManager::init door[" + dir + "]=" + dc);
			int rot = -90 * dir;
			// change below to getSize of sprite somehow
			float dw = 219;
			float dh = 224;
			float topOff = 32;
			float topSize = dh-topOff;
			float doorBorder = 36;

			float x = m_roomWidthPixels / 2 - dw / 2;
			float y = m_roomHeightPixels - dh;

			if (dc > 0) {
				Door d = new Door(dc, state);
				d.setName("Door" + dir);
				d.setSize(dw, dh);
				d.setRotation(rot);
				playground.addGameObject(d, null);

				ForegroundObject o = new ForegroundObject();
				o.setSprite("door_over");
				o.setName("DoorOver" + dir);
				o.setSize(dw, dh);
				o.setRotation(rot);
				playground.addGameObject(o, null);

				if (dir == 2) {
					// down

					Wall t1 = new Wall();
					t1.setSize(x+doorBorder, dh);
					t1.setPosition(0, 0);
					playground.addGameObject(t1, null);

					Wall t2 = new Wall();
					t2.setSize(x+doorBorder, dh);
					t2.setPosition(x + dw - doorBorder, 0);
					playground.addGameObject(t2, null);

					// rotated 180
					y = 0;

				} else if (dir == 1) {
					// right door
					y = m_roomHeightPixels / 2 - dw / 2;
					x = m_roomWidthPixels - dh;

					Wall t1 = new Wall();
					t1.setSize(dh, y+doorBorder);
					t1.setPosition(x, 0);
					playground.addGameObject(t1, null);

					Wall t2 = new Wall();
					t2.setSize(dh, y + doorBorder);
					t2.setPosition(x, y + dw-doorBorder);
					playground.addGameObject(t2, null);

					// adjust for rotation
					x -= (dw / 2 - dh / 2);
					//y += dw / 2;

				} else if (dir == 3) {
					// left door
					y = m_roomHeightPixels / 2 - dw / 2;
					x = 0;

					Wall t1 = new Wall();
					t1.setSize(dh, y + doorBorder);
					t1.setPosition(0, 0);
					playground.addGameObject(t1, null);

					Wall t2 = new Wall();
					t2.setSize(dh, y + doorBorder);
					t2.setPosition(0, y + dw - doorBorder);
					playground.addGameObject(t2, null);

					// rotation adjustment
					x -= (dw / 2 - dh / 2);
					//y += dw / 2;

				} else {
					// top
					Wall t1 = new Wall();
					t1.setSize(x + doorBorder, topSize);
					t1.setPosition(0, y+topOff);
					playground.addGameObject(t1, null);

					Wall t2 = new Wall();
					t2.setSize(x+ doorBorder, topSize);
					t2.setPosition(x + dw - doorBorder, y+topOff);
					playground.addGameObject(t2, null);
				}

				d.setPosition(x, y);
				o.setPosition(x, y);
				if (d.getKeyHole() != null)
				{
					d.getKeyHole().setRotation(rot);
					d.getKeyHole().setPosition(x,y);

					d.getDoorMat().setRotation(rot);
					if (dir == 0)
						d.getDoorMat().setPosition(x+30,y-5);
					else if (dir == 2)
						d.getDoorMat().setPosition(x+30,y+dh);
					else if (dir == 1)
						d.getDoorMat().setPosition(x-84,y+110);
					else if (dir == 3)
						d.getDoorMat().setPosition(x+144,y+110);
				}

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
					t1.setSize(m_roomWidthPixels, topSize);
					t1.setPosition(0, m_roomHeightPixels - topSize);
					playground.addGameObject(t1, null);
				}
			}
		}

		// TODO: Spawn Enemies
		// Place enemies based on TMX Spawn Objects?
		WormEnemy enemy = new WormEnemy();
		playground.addGameObject(enemy, KyperBoxGame.NULL_PROPERTIES);
		enemy.init(KyperBoxGame.NULL_PROPERTIES);
		randomPlaceObject(enemy, playground, level);

		ScorpionEnemy enemy2 = new ScorpionEnemy();
		playground.addGameObject(enemy2, KyperBoxGame.NULL_PROPERTIES);
		enemy2.init(KyperBoxGame.NULL_PROPERTIES);
		randomPlaceObject(enemy2, playground, level);
		enemy2.setPosition(enemy2.getX()+150,enemy2.getY());

		CherubEnemy enemy3 = new CherubEnemy();
		playground.addGameObject(enemy3, KyperBoxGame.NULL_PROPERTIES);
		enemy3.init(KyperBoxGame.NULL_PROPERTIES);
		randomPlaceObject(enemy3, playground, level);
		enemy3.setPosition(enemy3.getX()-250,enemy3.getY());

		
	  SpiderBossEnemy enemy4 = new SpiderBossEnemy();
		playground.addGameObject(enemy4, KyperBoxGame.NULL_PROPERTIES);
		enemy4.init(KyperBoxGame.NULL_PROPERTIES);
		randomPlaceObject(enemy4, playground, level);
		enemy4.setPosition(enemy4.getX()+250,enemy4.getY()-200);
	

		// Keys
		if (r.getHasKey())
		{
			//should place a key
			Collectible c = new Collectible();
			c.init(KyperBoxGame.NULL_PROPERTIES);
			c.setId(Collectible.KEY);
			playground.addGameObject(c, KyperBoxGame.NULL_PROPERTIES);
			randomPlaceObject(c, playground, level);
		}


		//Place bosses based on Room data


		//Place HUD
		GameLayer flayer = state.getForegroundLayer();
		HudMap mapHud = new HudMap(level);
		mapHud.updateLevel(level);
		mapHud.setRecommendedPosition(960, 540);
		flayer.addGameObject(mapHud, KyperBoxGame.NULL_PROPERTIES);
		
		//push overlay
		overlayManager = (OverlayManager) state.getGame().getState("gameOverlay").getManager();
		state.getGame().pushGameState("gameOverlay");
		overlayManager.fadeIn();
		overlayKeyAmount = player.m_numKeys;
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				overlayManager.setKeyLabelText(":"+overlayKeyAmount);
				overlayManager.getHealth().setProgress(player.getHealth().getHealthPercentage(), false);
			}
		});

		player.updateToCurrentForm();
		
	}

	public void saveRoomState(GameState state, Room r)
	{

		//Save Door state
		for (int i = 0; i < 4; i++)
		{
			Door d = (Door) state.getPlaygroundLayer().getGameObject("Door" + i);
			if (d != null)
			{
				r.setDoor(i, d.getCode());
			}
		}

		//Collectible state?

		//Enemy state?

		//Traps state?


	}

	@Override
	public void update(GameState state, float delta) {
		if(overlayKeyAmount!=player.m_numKeys) {
			boolean pulse = player.m_numKeys > overlayKeyAmount;
			overlayKeyAmount = player.m_numKeys;
			overlayManager.updateKeys(overlayKeyAmount,pulse);
		}
		
		HealthControl health = player.getHealth();
		HealthBauble hp = overlayManager.getHealth();
		if(hp!=null) {
			if(hp.getProgress() != health.getHealthPercentage())
				overlayManager.updateHealth(health.getHealthPercentage());
		}
		
		if(health.shouldDie() && !died) {
			overlayManager.fadeOut();
			died = true;
		}
		
		if(player.isRemoved()) {
			getState().getGame().setGameState("gameover");
		}
	}

}
