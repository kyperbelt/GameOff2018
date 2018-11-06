package com.gameoff.game.managers;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.gameoff.game.systems.DeathSystem;
import com.gameoff.game.systems.MoveSystem;
import com.gameoff.game.systems.OutOfBoundsSystem;
import com.gameoff.game.systems.PlayerCameraSystem;
import com.gameoff.game.systems.PlayerControlSystem;
import com.gameoff.game.systems.YSortSystem;
import com.gameoff.game.GameLevel;
import com.gameoff.game.Room;
import com.kyperbox.GameState;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.GameLayer;
import com.kyperbox.systems.QuadTree;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.umisc.IGameObjectFactory;
import com.gameoff.game.objects.*;

public class LevelManager extends StateManager {

	// test player for now
	Player player;

	// layer systems we will use for playground layer
	QuadTree quad;// collisionSystem
	MoveSystem move;
	PlayerControlSystem control;
	PlayerCameraSystem camera;
	DeathSystem death;
	YSortSystem ysort;
	OutOfBoundsSystem bounds;
	int m_roomWidthPixels, m_roomHeightPixels;

	@Override
	public void addLayerSystems(GameState state) {
		//get the playground layer from the state
		GameLayer playground = state.getPlaygroundLayer();
		
		//get the map data and a tile layer to set the world size of the level. 
		TiledMapTileLayer floor = (TiledMapTileLayer) state.getMapData().getLayers().get("floor_tiles");
		m_roomWidthPixels = (int) (floor.getWidth() * floor.getTileWidth());
		m_roomHeightPixels = (int) (floor.getHeight() * floor.getTileHeight());
		
		//set the size of this quad tree. Anything outside the bounds will not have collision detection
		quad = new QuadTree(m_roomWidthPixels, m_roomHeightPixels);
		
		move = new MoveSystem();
		control = new PlayerControlSystem();
		
		//the camera has bounds that it will not move past
		camera = new PlayerCameraSystem(m_roomWidthPixels, m_roomHeightPixels);
		
		death = new DeathSystem();
		
		ysort = new YSortSystem();
		
		//ssystem to detect when a player is out of bounds and then transition room
		bounds = new OutOfBoundsSystem(0, 0, m_roomWidthPixels, m_roomHeightPixels);
		
		//add all the systems to the playground layer. If we want things like collision on a separate layer 
		//then we must add systems(unique) to that layer as well. 
		playground.addLayerSystem(control);
		playground.addLayerSystem(quad);
		playground.addLayerSystem(move);
		playground.addLayerSystem(camera);
		playground.addLayerSystem(death);
		playground.addLayerSystem(ysort);
		playground.addLayerSystem(bounds);
	}

	@Override
	public void dispose(GameState state) {
		//reset all projectiles & melee attack pools
		Projectile.resetProjectiles();
		MeleeAttack.resetMeleeAttacks();
	}

	@Override
	public void init(GameState state) {

		//When this is called, TMX is loaded, objects created, etc.
		//and this is called right before being shown.
		GameLayer playground = state.getPlaygroundLayer();

		//retrieve the player object from the playground layer.
		player = (Player) playground.getGameObject("player");

		KyperBoxGame game = state.getGame();
		GameLevel level = GameLevel.getCurrentLevel();

		Room r = level.getCurrentRoom();
		//IGameObjectFactory factory = game.getObjectFactory();

		//Place Doors & Walls
		//Not sure about collision box rotation/how that all works
		//Might be smarter to play with origin for rotation, this
		//is a bit hacky ;)
		for (int dir = 0; dir < 4; dir++)
		{
			int dc = r.getDoor(dir);
			int rot = -90 * dir;
			//change below to getSize of sprite somehow
			float dw = 64;
			float dh = 32;

			float x = m_roomWidthPixels/2 - dw/2;
			float y = m_roomHeightPixels - dh;

			if (dc > 0)
			{
				Door d = new Door(dc);
				d.setName("Door" + dir);
				d.setSize(dw,dh);
				d.setRotation(rot);
				playground.addGameObject(d,null);

				if (dir == 2) 
				{
					//down
					
					Wall t1 = new Wall();
					t1.setSize(x,dh);
					t1.setPosition(0,0);
					playground.addGameObject(t1,null);

					Wall t2 = new Wall();
					t2.setSize(x,dh);
					t2.setPosition(x+dw,0);
					playground.addGameObject(t2,null);

					//rotated 180
					y = 0;

				} else if (dir == 1)
				{
					//right door
					y = m_roomHeightPixels/2 - dw/2;
					x = m_roomWidthPixels - dh;
					
					Wall t1 = new Wall();
					t1.setSize(dh,y);
					t1.setPosition(x,0);
					playground.addGameObject(t1,null);

					Wall t2 = new Wall();
					t2.setSize(dh,y);
					t2.setPosition(x, y + dw);
					playground.addGameObject(t2,null);

					//adjust for rotation
					x -= dh/2;
					y += dw/4;

				}	else if (dir == 3)
				{
					//left door
					y = m_roomHeightPixels/2 - dw/2;
					x = 0;

					Wall t1 = new Wall();
					t1.setSize(dh,y);
					t1.setPosition(0,0);
					playground.addGameObject(t1,null);

					Wall t2 = new Wall();
					t2.setSize(dh,y);
					t2.setPosition(0, y + dw);
					playground.addGameObject(t2,null);

					//rotation adjustment
					x -= dh/2;
					y += dw/4;

				} else
				{
					//top
					Wall t1 = new Wall();
					t1.setSize(x,dh);
					t1.setPosition(0,y);
					playground.addGameObject(t1,null);

					Wall t2 = new Wall();
					t2.setSize(x,dh);
					t2.setPosition(x+dw,y);
					playground.addGameObject(t2,null);
				}			
				d.setPosition(x,y);

			} else
			{
				//just place full walls
				if (dir == 2) 
				{
					Wall t1 = new Wall();
					t1.setSize(m_roomWidthPixels,dh);
					t1.setPosition(0,0);
					playground.addGameObject(t1,null);
				} else if (dir == 1)
				{
					//right door
					Wall t1 = new Wall();
					t1.setSize(dh,m_roomHeightPixels);
					t1.setPosition(m_roomWidthPixels-dh,0);
					playground.addGameObject(t1,null);

				}	else if (dir == 3)
				{
					//left door
					Wall t1 = new Wall();
					t1.setSize(dh,m_roomHeightPixels);
					t1.setPosition(0,0);
					playground.addGameObject(t1,null);
				} else
				{
					//top
					Wall t1 = new Wall();
					t1.setSize(m_roomWidthPixels,dh);
					t1.setPosition(0,m_roomHeightPixels-dh);
					playground.addGameObject(t1,null);
				}		
			}
		}


		//TODO: Spawn Enemies
		//Place enemies based on TMX Spawn Objects?

		//TODO: Keys
		//Place room keys and bosses based on Room date

		GameLayer flayer = state.getForegroundLayer();
		HudMap mapHud = new HudMap(level);
		mapHud.updateLevel(level);
		mapHud.setRecommendedPosition(960,540);
		flayer.addGameObject(mapHud,KyperBoxGame.NULL_PROPERTIES);
	}

	@Override
	public void update(GameState state, float delta) {
		
		
		
	}

}
