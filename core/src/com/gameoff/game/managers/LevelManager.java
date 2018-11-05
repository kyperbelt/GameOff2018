package com.gameoff.game.managers;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.gameoff.game.objects.MeleeAttack;
import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.Projectile;
import com.gameoff.game.systems.DeathSystem;
import com.gameoff.game.systems.MoveSystem;
import com.gameoff.game.systems.PlayerCameraSystem;
import com.gameoff.game.systems.PlayerControlSystem;
import com.gameoff.game.systems.YSortSystem;
import com.kyperbox.GameState;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.GameLayer;
import com.kyperbox.systems.QuadTree;

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

	@Override
	public void addLayerSystems(GameState state) {
		//get the playground layer from the state
		GameLayer playground = state.getPlaygroundLayer();
		
		//get the map data and a tile layer to set the world size of the level. 
		TiledMapTileLayer floor = (TiledMapTileLayer) state.getMapData().getLayers().get("floor_tiles");
		int width = (int) (floor.getWidth() * floor.getTileWidth());
		int height = (int) (floor.getHeight() * floor.getTileHeight());
		
		//set the size of this quad tree. Anything outside the bounds will not have collision detection
		quad = new QuadTree(width,height);
		
		move = new MoveSystem();
		control = new PlayerControlSystem();
		
		//the camera has bounds that it will not move past
		camera = new PlayerCameraSystem(width, height);
		
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
		Projectile.resetProjectTiles();
		MeleeAttack.resetMeleeAttacks();
	}

	@Override
	public void init(GameState state) {
		GameLayer playground = state.getPlaygroundLayer();

		//retrieve the player object from the playground layer.
		player = (Player) playground.getGameObject("player");

	}

	@Override
	public void update(GameState state, float delta) {
		
	}

}
