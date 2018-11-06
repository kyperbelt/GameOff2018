package com.gameoff.game.managers;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.gameoff.game.systems.DeathSystem;
import com.gameoff.game.systems.MoveSystem;
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
		
		//add all the systems to the playground layer. If we want things like collision on a separate layer 
		//then we must add systems(unique) to that layer as well. 
		playground.addLayerSystem(control);
		playground.addLayerSystem(quad);
		playground.addLayerSystem(move);
		playground.addLayerSystem(camera);
		playground.addLayerSystem(death);
		playground.addLayerSystem(ysort);
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

		//Place Doors
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
			if (dc > 0)
			{
				Door d = new Door(dc);
				d.setName("Door" + dir);
				playground.addGameObject(d,null);

				d.setSize(dw,dh);
				d.setRotation(rot);
				float x = m_roomWidthPixels/2 - dw/2;
				float y = m_roomHeightPixels - dh;
				if (dir == 2) 
				{
					//down
					y = 0;
					y = dh;
				} else if (dir == 1)
				{
					//right door
					y = m_roomHeightPixels/2 + dw/2;
					x = m_roomWidthPixels - dh;
				}	else if (dir == 3)
				{
					//left door
					y = m_roomHeightPixels/2 + dw/2;
					x = dh;
				}			
				d.setPosition(x,y);
			}
		}

		//TODO: Spawn Enemies
		//Place enemies based on TMX Spawn Objects?

		//TODO: Keys
		//Place room keys and bosses based on Room date

	}

	@Override
	public void update(GameState state, float delta) {
		
	}

}
