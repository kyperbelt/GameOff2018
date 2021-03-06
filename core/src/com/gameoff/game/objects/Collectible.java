package com.gameoff.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.kyperbox.GameState;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.umisc.KyperSprite;

public class Collectible extends Basic {
	// -pool stuff
	private static Pool<Collectible> pool = new Pool<Collectible>(100) {
		@Override
		protected Collectible newObject() {
			return new Collectible();
		}
	};

	private static Array<Collectible> used = new Array<Collectible>();

	public static Collectible get(int id) {
		Collectible c = pool.obtain();
		c.angle = MathUtils.random() * MathUtils.PI2;
		used.add(c);
		c.setId(id);
		return c;
	}

	public static void freeAll() {
		pool.freeAll(used);
		used.clear();
	}

	// ---

	// collectible ids
	public static final int NONE = -1; //no collectible
	
	public static final int KEY = 0;
	public static final int HEART = 1;
	public static final int SHIELD = 2;
	public static final int SWORD = 3;
	public static final int SOUL = 10;

	// ---collectible id end

	private String animationName = KyperBoxGame.NULL_STRING;

	private static final float MINDIST = 20f;
	private static final float MAXDIST = 60f;

	private float angle = 0;

	private AnimationController anim;

	int id = KEY;
	boolean collected = false;
	boolean collectible = false;

	public Collectible() {

		anim = new AnimationController();
	}
	
	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		animationName = KyperBoxGame.NULL_STRING;
		getColor().a = 1f;
		setVisible(true);
		removeController(getAnimation());
		collected = false;
		getMove().setPhysical(false);
		getHealth().setCurrentHealth(getHealth().getMaxHealth());
		collectible = false;
		clearActions();

		float dist = MathUtils.random(MINDIST, MAXDIST);
		float xx = MathUtils.cos(angle) * dist;
		float yy = MathUtils.sin(angle) * dist;
		Action popAction = Actions.moveTo(getX() + xx, getY() + yy, (dist / MAXDIST) * 1f, Interpolation.circleOut);

		float bobAmount = MathUtils.random(5, 8);
		float bobTime = MathUtils.random(.5f, .8f);
		Action bobAction = Actions.sequence(Actions.moveBy(0, bobAmount * .5f, bobTime * .5f), Actions.repeat(-1,
				Actions.sequence(Actions.moveBy(0, -bobAmount, bobTime), Actions.moveBy(0, bobAmount, bobTime))));

		addAction(Actions.sequence(popAction, new Action() {
			@Override
			public boolean act(float delta) {
				collectible = true;
				return true;
			}
		}, bobAction));

		if (properties != null) {
			setId(properties.get("item_id", id, Integer.class));
		} else {
			setId(id);
		}

		if (!animationName.equals(KyperBoxGame.NULL_STRING)) {
			anim.setAnimation(animationName, PlayMode.LOOP);
			System.out.println("animation added :" + animationName);
			addController(anim);
		}
		System.out.println("createc collectible x:" + getX() + " y:" + getY() + " id:" + getId());
	}

	public boolean isCollectible() {
		return collectible;
	}

	@Override
	public void update(float delta) {
		super.update(delta);
	}

	public void setId(int id) {
		this.id = id;
		if (getGameLayer() != null) {

			removeController(anim);
			GameState state = getState();
			switch (id) {
			case KEY:

				Sprite sprite = getState().getGameSprite("key");
				setSize(sprite.getWidth(), sprite.getHeight());
				setCollisionBounds(0, 0, sprite.getWidth(), sprite.getHeight());
				setSprite("key");
				break;
			case HEART:
				Sprite heart = getState().getGameSprite("heart_0");
				setSize(heart.getWidth(), heart.getHeight());
				Animation<KyperSprite> ha = state.getAnimation("heart");
				if (ha == null)
					state.storeAnimation("heart", state.createGameAnimation("heart", .17f));

				anim.setAnimation("heart", PlayMode.LOOP);
				addController(anim);
				animationName = "heart";
				setCollisionBounds(0, 0, heart.getWidth(), heart.getHeight());
				break;
			case SHIELD:
				Sprite shield = getState().getGameSprite("shield_0");
				setSize(shield.getWidth(), shield.getHeight());
				Animation<KyperSprite> sa = state.getAnimation("shield");
				if (sa == null)
					state.storeAnimation("shield", state.createGameAnimation("shield", .17f));

				anim.setAnimation("shield", PlayMode.LOOP);
				addController(anim);
				animationName = "shield";
				setCollisionBounds(0, 0, shield.getWidth(), shield.getHeight());
				break;
			case SWORD:
				Sprite sword = getState().getGameSprite("sword_0");
				setSize(sword.getWidth(), sword.getHeight());
				Animation<KyperSprite> ssa = state.getAnimation("sword");
				if (ssa == null)
					state.storeAnimation("sword", state.createGameAnimation("sword", .17f));

				anim.setAnimation("sword", PlayMode.LOOP);
				addController(anim);
				animationName = "sword";
				setCollisionBounds(0, 0, sword.getWidth(), sword.getHeight());
				break;
			case SOUL:
				Sprite soul = getState().getGameSprite("soul_0");
				// setSprite("soul_0");
				setSize(soul.getWidth(), soul.getHeight());
				Animation<KyperSprite> a = state.getAnimation("soulAnim");
				if (a == null)
					state.storeAnimation("soulAnim", state.createGameAnimation("soul", .17f));
				anim.setAnimation("soulAnim", PlayMode.LOOP);
				addController(anim);

				animationName = "soulAnim";
				setCollisionBounds(0, 0, soul.getWidth(), soul.getHeight());
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

	@Override
	public void onRemove() {
		super.onRemove();

		pool.free(this);
		used.removeValue(this, true);
		System.out.println("removed collectible");

	}

	public boolean isCollected() {
		return collected;
	}

}
