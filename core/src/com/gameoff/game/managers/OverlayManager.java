package com.gameoff.game.managers;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.gameoff.game.objects.HealthBauble;
import com.kyperbox.GameState;
import com.kyperbox.managers.StateManager;

public class OverlayManager extends StateManager{

	private float fadeinTime = .8f;
	
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
		health.setAlpha(.7f);
		
		
	}
	
	public void fadeIn() {
		GameState state = getState();
		state.getColor().a = 0f;
		state.clearActions();
		
		state.addAction(Actions.fadeIn(fadeinTime));
	}
	
	public void fadeOut() {
		GameState state = getState();
		state.clearActions();
		
		state.addAction(Actions.fadeOut(fadeinTime));
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
	
	public void updateKeys(int keys,boolean pulse) {
		setKeyLabelText(":"+keys);
		if(pulse) {
			keyIcon.clearActions();
			keyIcon.setScale(1f);
			keyIcon.addAction(Actions.sequence( Actions.scaleTo(1.5f, 1.5f,.25f),Actions.scaleTo(1f, 1f,.25f)));
		}
	}
	
	public void updateHealth(float progress) {
		if(health!=null)
			health.setProgress(progress);
	}
	
	public HealthBauble getHealth() {
		return health;
	}
	
}
