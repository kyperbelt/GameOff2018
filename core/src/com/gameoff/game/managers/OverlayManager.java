package com.gameoff.game.managers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.gameoff.game.objects.HealthBauble;
import com.gameoff.game.objects.ProgressTexture;
import com.kyperbox.GameState;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.managers.StateManager;

public class OverlayManager extends StateManager{

	private Image keyIcon;
	private Label keyLabel;
	
	
	private HealthBauble health;
	
	@Override
	public void addLayerSystems(GameState state) {
		state.shouldHaltUpdate(false);//does not hault the update of the under layers
	}

	@Override
	public void init(GameState state) {
		keyIcon = (Image) state.getUiLayer().getActor("key");
		keyLabel = (Label) state.getUiLayer().getActor("keyLabel");
		health = (HealthBauble) state.getUiLayer().getActor("health");
		
		
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
	
	public void updateHealth(float progress) {
		if(health!=null)
			health.setProgress(progress);
	}
	
	public HealthBauble getHealth() {
		return health;
	}
	
}
