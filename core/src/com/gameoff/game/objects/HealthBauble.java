package com.gameoff.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.kyperbox.GameState;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.BakedEffects;
import com.kyperbox.umisc.KyperSprite;

public class HealthBauble extends GameObject {

	BasicGameObject overlay;
	AnimationController overlayAnim;
	ProgressTexture health;
	AnimationController healthAnim;
	BasicGameObject back;

	float startProgress;
	float currentProgress;

	Interpolation tween = Interpolation.linear;
	float time = .5f;
	float elapsed = 0;

	boolean tweening;

	public HealthBauble() {
		overlay = new BasicGameObject();
		overlayAnim = new AnimationController();
		health = new ProgressTexture();
		healthAnim = new AnimationController();
		back = new BasicGameObject();
	}

	public void setProgress(float progress) {
		setProgress(progress, true);
	}

	public void setProgress(float progress, boolean tween) {
		
	
		
		currentProgress = progress;
		startProgress = health.getProgress();
		

		if (tween) {
			tweening = true;
			elapsed = 0;
			if(currentProgress < startProgress) {
				clearActions();
				addAction(BakedEffects.shake(.3f, 10, false, true));
			}
		}else {
			health.setProgress(progress);
		}
	}

	public float getProgress() {
		return currentProgress;
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		GameState state = getState();

		Animation<KyperSprite> anim = state.getAnimation("health");
		if (anim == null) {
			// state.storeAnimation("health", state.createGameAnimation("health", .2f));
		}
		anim = state.getAnimation("health_overlay");
		if (anim == null) {
			// state.storeAnimation("health_overlay",
			// state.createGameAnimation("health_overlay", .2f));
		}

		back.setSize(getWidth(), getHeight());
		back.setSprite("health_back");

		overlay.setSize(getWidth(), getHeight());
		overlay.setSprite("health_overlay_0");
		// overlayAnim.setAnimation("health_overlay",PlayMode.LOOP_RANDOM);
		// overlay.addController(overlayAnim);

		health.setSize(getWidth() * .30f, getHeight() * .48f);
		health.setPosition(getWidth() * .5f - health.getWidth() * .5f - getWidth() * .015f,
				getHeight() * .5f - health.getHeight() * .5f + getHeight() * .02f);
		health.setSprite("health_0");
		// healthAnim.setAnimation("health",PlayMode.LOOP);
		// health.addController(healthAnim);

		addChild(back);

		addChild(health);
		addChild(overlay);

	}

	@Override
	public void update(float delta) {
		super.update(delta);

		if (tweening) {
			elapsed += delta;
			float t = elapsed / time;
			float p = tween.apply(startProgress, currentProgress, t);
			health.setProgress(p);
			if (elapsed >= time) {
				tweening = false;
			}
		}
	}
}
