package com.gameoff.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.MapProperties;
import com.kyperbox.GameState;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.KyperSprite;

public class HealthBauble extends GameObject{
	
	BasicGameObject overlay;
	AnimationController overlayAnim;
	ProgressTexture health;
	AnimationController healthAnim;
	BasicGameObject back;
	
	public HealthBauble() {
		overlay = new BasicGameObject();
		overlayAnim = new AnimationController();
		health = new ProgressTexture();
		healthAnim = new AnimationController();
		back = new BasicGameObject();
	}
	
	public void setProgress(float progress) {
		health.setProgress(progress);
	}
	
	public float getProgress() {
		return health.getProgress();
	}
	
	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		GameState state = getState();
		
		
		Animation<KyperSprite> anim = state.getAnimation("health");
		if(anim==null) {
			//state.storeAnimation("health", state.createGameAnimation("health", .2f));
		}
		anim = state.getAnimation("health_overlay");
		if(anim == null) {
		//	state.storeAnimation("health_overlay", state.createGameAnimation("health_overlay", .2f));
		}
		
		back.setSize(getWidth(), getHeight());
		back.setSprite("health_back");
		
		overlay.setSize(getWidth(),getHeight());
		overlay.setSprite("health_overlay_0");
		//overlayAnim.setAnimation("health_overlay",PlayMode.LOOP_RANDOM);
		//overlay.addController(overlayAnim);
		
		health.setSize(getWidth(), getHeight());
		health.setSprite("health_0");
		//healthAnim.setAnimation("health",PlayMode.LOOP);
		//health.addController(healthAnim);
		
		addChild(back);
		addChild(health);
		addChild(overlay);
		
	}

	
	@Override
	public void update(float delta) {
		super.update(delta);
	}
}
