package com.gameoff.game.managers;

import com.gameoff.game.GameOffGame;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.objects.Player;
import com.gameoff.game.systems.MoveSystem;
import com.gameoff.game.systems.PlayerControlSystem;
import com.kyperbox.GameState;
import com.kyperbox.input.InputDefaults;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.GameLayer;
import com.kyperbox.systems.QuadTree;

public class LevelManager extends StateManager {

	// test player for now
	Player player;

	// layer systems we will use for playground layer
	QuadTree quad;// collisionSystem
	MoveSystem move;
	PlayerControlSystem control = new PlayerControlSystem();
	// YSortSystem ysort;

	@Override
	public void addLayerSystems(GameState state) {
		GameLayer playground = state.getPlaygroundLayer();
		quad = new QuadTree(GameOffGame.WIDTH, GameOffGame.HEIGHT);
		move = new MoveSystem();
		control = new PlayerControlSystem();
		
		playground.addLayerSystem(control);
		playground.addLayerSystem(quad);
		playground.addLayerSystem(move);
	}

	@Override
	public void dispose(GameState state) {

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
