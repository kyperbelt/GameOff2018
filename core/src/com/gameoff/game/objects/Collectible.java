package com.gameoff.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kyperbox.umisc.KyperSprite;

public class Collectible extends Basic {

	public static final int KEY = 0;
	public static final int SOUL = 10;

	int id = KEY;
	boolean collected = false;

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		collected = false;
		getMove().setPhysical(false);

		clearActions();

		float bobAmount = MathUtils.random(5, 8);
		float bobTime = MathUtils.random(.5f, .8f);

		addAction(Actions.sequence(Actions.moveBy(0, bobAmount * .5f, bobTime * .5f), Actions.repeat(-1,
				Actions.sequence(Actions.moveBy(0, -bobAmount, bobTime), Actions.moveBy(0, bobAmount, bobTime)))));

		if (properties != null)
		{
			setId(properties.get("item_id", id, Integer.class));
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);

	}

	public void setId(int id) {
		this.id = id;
		if (getGameLayer() != null) {
			switch (id) {
			case KEY:

				Sprite sprite = getState().getGameSprite("key");
				setSize(sprite.getWidth(), sprite.getHeight());
				setSprite("key");
				break;
			case SOUL:
				setSprite("noregion");
				break;
			}
		}
	}

	public int getId() {
		return id;
	}

	public void collect() {
		collected = true;
		clearActions();
		float duration = .6f;
		addAction(Actions.sequence(Actions.parallel(Actions.moveBy(0, 50, duration), Actions.fadeOut(duration)),
				Actions.removeActor()

		));

	}

	public boolean isCollected() {
		return collected;
	}

}
