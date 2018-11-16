package com.gameoff.game.objects.destructible;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.gameoff.game.control.HealthControl.DamageListener;
import com.gameoff.game.control.HealthControl.DeathListener;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.objects.Basic;
import com.gameoff.game.objects.Collectible;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.controllers.AnimationController.AnimationListener;
import com.kyperbox.umisc.BakedEffects;
import com.kyperbox.umisc.KyperSprite;

public class Destructible extends Basic {

	Action shake;
	int drop;// drops
	String destroyAnimation;
	String sprite = "noregion";

	boolean animationPlaying = false;
	boolean animationPresent = false;
	boolean destroyed = false;

	AnimationListener alistener = new AnimationListener() {

		@Override
		public void finished(String animation, int times) {
			if (times >= 1) {
				destroyed = true;

				// drop item here
				{
					Collectible item = new Collectible();
					item.setId(drop);
					Vector2 center = getCollisionCenter();
					item.setPosition(center.x, center.y);

					getGameLayer().addGameObject(item, KyperBoxGame.NULL_PROPERTIES);

					item.setBounds(0, 0, item.getWidth(), item.getHeight());
				}
				// ---end drop item
				getAnimation().setListener(null);
			}
		}
	};

	public Destructible() {
		shake = BakedEffects.shake(.5f, 5, false, true);
		drop = Collectible.SOUL;

		setSprite(sprite);
		destroyAnimation = null;
		getHealth().setHealthGroup(HealthGroup.Neutral);
		getHealth().setDeathListener(new DeathListener() {
			@Override
			public boolean die(float delta) {
				if (animationPresent) {

					if (!animationPlaying) {
						addController(getAnimation());
						getAnimation().set(destroyAnimation);
						getAnimation().setListener(alistener);
						animationPlaying = true;
					}
				} else {
					destroyed = true;
				}
				return destroyed;
			}
		});

		getHealth().setDamageListener(new DamageListener() {
			@Override
			public void damaged(float amount) {
				clearActions();
				shake.restart();
				addAction(shake);
			}
		});

	}

	public void setHealth(float health) {
		getHealth().setMaxHealth(health);
	}

	@Override
	public void setSprite(String sprite) {
		this.sprite = sprite;
		super.setSprite(sprite);
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		removeController(getAnimation());
		removeController(getMove());
		if (properties != null) {
			setHealth(properties.get("health", getHealth().getCurrentHealth(), Float.class));
			drop = properties.get("itemDrop", drop, Integer.class);
			setSprite(properties.get("sprite", sprite, String.class));
			destroyAnimation = properties.get("DestroyAnimation", destroyAnimation, String.class);
		}

		if (destroyAnimation != null && !destroyAnimation.isEmpty()) {

			Animation<KyperSprite> anim = getState().getAnimation(destroyAnimation);
			if (anim == null) {
				getState().storeAnimation(destroyAnimation, getState().createGameAnimation(destroyAnimation, .13f));
			}
			getAnimation().addAnimation(destroyAnimation, destroyAnimation);
			animationPresent = true;

		}
		animationPlaying = false;
		destroyed = false;
	}

}