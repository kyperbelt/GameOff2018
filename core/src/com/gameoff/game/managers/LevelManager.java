package com.gameoff.game.managers;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.gameoff.game.GameOffGame;
import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.Projectile;
import com.gameoff.game.systems.MoveSystem;
import com.gameoff.game.systems.PlayerCameraSystem;
import com.gameoff.game.systems.PlayerControlSystem;
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
	// YSortSystem ysort;

	@Override
	public void addLayerSystems(GameState state) {
		GameLayer playground = state.getPlaygroundLayer();
		TiledMapTileLayer floor = (TiledMapTileLayer) state.getMapData().getLayers().get("floor_tiles");
	
		int width = (int) (floor.getWidth() * floor.getTileWidth());
		int height = (int) (floor.getHeight() * floor.getTileHeight());
		quad = new QuadTree(width,height);
		
		move = new MoveSystem();
		control = new PlayerControlSystem();
		camera = new PlayerCameraSystem(width, height);
		
		playground.addLayerSystem(control);
		playground.addLayerSystem(quad);
		playground.addLayerSystem(move);
		playground.addLayerSystem(camera);
	}

	@Override
	public void dispose(GameState state) {
		//reset all projectiles
		Projectile.resetProjectTiles();
	}

	@Override
	public void init(GameState state) {
		GameLayer playground = state.getPlaygroundLayer();

		player = (Player) playground.getGameObject("player");

	}

	@Override
	public void update(GameState state, float delta) {
		
	}

}
