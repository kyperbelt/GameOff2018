package com.gameoff.game.managers;

import com.kyperbox.GameState;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.BasicGameObject;

public class GameOverManager extends StateManager{
	
	
	BasicGameObject youdied;
	
	
	@Override
	public void addLayerSystems(GameState state) {
		
	}

	@Override
	public void init(GameState state) {
		youdied = (BasicGameObject) state.getForegroundLayer().getGameObject("youdied");
	}

	@Override
	public void update(GameState state, float delta) {
		
	}

	@Override
	public void dispose(GameState state) {
		
	}

}
