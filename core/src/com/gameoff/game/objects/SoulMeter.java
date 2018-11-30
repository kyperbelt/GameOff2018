package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.kyperbox.GameState;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.BakedEffects;

public class SoulMeter extends GameObject{
	
	ProgressTexture souls;
	BasicGameObject back;

	float startProgress;
	float currentProgress;

	Interpolation tween = Interpolation.linear;
	float time = .5f;
	float elapsed = 0;

	boolean tweening;

	public SoulMeter() {
		souls = new ProgressTexture();
		back = new BasicGameObject();
	}

	public void setProgress(float progress) {
		setProgress(progress, true);
	}

	public void setProgress(float progress, boolean tween) {
		
	
		
		currentProgress = progress;
		startProgress = souls.getProgress();
		

		if (tween) {
			tweening = true;
			elapsed = 0;
			if(currentProgress < startProgress) {
				clearActions();
				//addAction(BakedEffects.shake(.1f, 10, false, true));
			}
		}else {
			souls.setProgress(progress);
		}
	}

	public float getProgress() {
		return currentProgress;
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		GameState state = getState();

	

		back.setSize(getWidth(), getHeight());
		back.setSprite("soulMeter_back");


		souls.setSize(getWidth(), getHeight());
		souls.setSprite("soulMeter");
		addChild(back);

		addChild(souls);
	}

	@Override
	public void update(float delta) {
		super.update(delta);

		if (tweening) {
			elapsed += delta;
			float t = elapsed / time;
			float p = tween.apply(startProgress, currentProgress, t);
			souls.setProgress(p);
			if (elapsed >= time) {
				tweening = false;
			}
		}
	}

}
