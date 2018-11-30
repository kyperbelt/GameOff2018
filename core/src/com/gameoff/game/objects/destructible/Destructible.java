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
import com.gameoff.game.*;

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

				if (((drop < 2)  || (drop == 10)) && (GameLevel.getCurrentLevel().getCurrentRoom().getVisited()))
				{
					//don't drop hearts, souls or keys if room already visited
					return;
				}

				// drop item here
				if (drop >= 0)
				{
					Collectible item = Collectible.get(drop);
					Vector2 center = getCollisionCenter();
					item.setPosition(center.x, center.y);
					getGameLayer().addGameObject(item, KyperBoxGame.NULL_PROPERTIES);
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
		//removeController(getMove());
		getMove().setPassable(false);
		float boundX = 0;
		float boundY = 0;

		if (properties != null) {
			setHealth(properties.get("health", getHealth().getCurrentHealth(), Float.class));
			drop = properties.get("itemDrop", drop, Integer.class);
			setSprite(properties.get("sprite", sprite, String.class));
			destroyAnimation = properties.get("DestroyAnimation", destroyAnimation, String.class);
			boundX = properties.get("boundX", 0f, Float.class);
			boundY = properties.get("boundY", 0f, Float.class);
			float xadj = boundX*getWidth()/2f;
			float yadj = boundY*getHeight()/2f;
			setBounds(xadj,yadj,getWidth()-xadj*2, getHeight()-yadj*2);
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
