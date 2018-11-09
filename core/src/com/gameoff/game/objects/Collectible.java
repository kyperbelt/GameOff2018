package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Collectible extends Basic {

	public static final int KEY = 0;
	public static final int SOUL = 10;

	int id;
	boolean collected = false;

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		collected = false;
		getMove().setPhysical(false);

		clearActions();
		
		float bobAmount = MathUtils.random(5, 8);
		float bobTime = MathUtils.random(.5f,.8f);
		
		addAction(Actions.sequence(Actions.moveBy(0, bobAmount * .5f, bobTime * .5f), Actions.repeat(-1,
				Actions.sequence(Actions.moveBy(0, -bobAmount, bobTime), Actions.moveBy(0, bobAmount, bobTime)))));
		
		setId(properties.get("item_id", KEY, Integer.class));
	}
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
	}

	public void setId(int id) {
		this.id = id;
		switch (id) {
		case KEY:
			setSprite("noregion");
			break;
		case SOUL:
			setSprite("noregion");
			break;
		}
	}
	
	public int getId() {
		return id;
	}
	
	public void collect() {
		collected = true;
		clearActions();
		float duration = .6f;
		addAction(Actions.sequence(
				Actions.parallel(
						Actions.moveBy(0, 50,duration),
						Actions.fadeOut(duration)
						),
				Actions.removeActor()
				
				));
		
	}
	
	public boolean isCollected() {
		return collected;
	}

}