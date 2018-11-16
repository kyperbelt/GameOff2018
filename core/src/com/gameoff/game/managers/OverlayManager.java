package com.gameoff.game.managers;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kyperbox.GameState;
import com.kyperbox.managers.StateManager;

public class OverlayManager extends StateManager{

	Image keyIcon;
	Label keyLabel;
	
	@Override
	public void addLayerSystems(GameState state) {
		state.shouldHaltUpdate(false);//does not hault the update of the under layers
	}

	@Override
	public void init(GameState state) {
		keyIcon = (Image) state.getUiLayer().getActor("key");
		keyLabel = (Label) state.getUiLayer().getActor("keyLabel");
	}

	@Override
	public void update(GameState state, float delta) {
		
	}

	@Override
	public void dispose(GameState state) {
		
	}
	
	public void setKeyLabelText(String text) {
		if(keyLabel!=null)
			keyLabel.setText(text);
	}
	
	public void updateKeys(int keys) {
		setKeyLabelText(":"+keys);
	}
	
}
