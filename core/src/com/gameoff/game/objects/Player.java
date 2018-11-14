package com.gameoff.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.control.AttackControl;
import com.gameoff.game.control.AttackControl.AttackListener;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.DirectionControl.DirectionChangeListener;
import com.gameoff.game.control.HealthControl.DamageListener;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.PlayerControl;
import com.kyperbox.GameState;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.controllers.AnimationController.AnimationListener;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.KyperSprite;
import com.kyperbox.umisc.StringUtils;

public class Player extends DirectionEntity {

	public static float WIDTH = 144 * .75f;
	public static float HEIGHT = 160 * .75f;

	// animation literals
	public static final String SEP = "_";
	public static final String ANGEL = "aform";
	public static final String DEMON = "dform";

	public static final String WALKDOWN = "walk_down";
	public static final String WALKUP = "walk_up";

	public static final String ATTACKUP = "attack_up";
	public static final String ATTACKDOWN = "attack_down";
	public static final String ATTACKSIDE = "attack_side";

	public static final String LEGS = "legs";
	public static final String SIDE = "side";

	public static final String ANGELWALKDOWN = ANGEL + SEP + WALKDOWN;

	public static final String DEMONWALKDOWN = DEMON + SEP + WALKDOWN;
	public static final String DEMONWALKUP = DEMON + SEP + WALKUP;
	public static final String DEMONWALKSIDE = DEMON + "_walk_" + SIDE;
	public static final String DEMONLEGS = DEMON + SEP + LEGS;
	public static final String DEMONLEGSSIDE = DEMONLEGS + SIDE;
	public static final String DEMONATTACKSIDE = DEMON + SEP + ATTACKSIDE;

	public int m_numKeys = 0;

	public enum PlayerState {
		Idling, Moving, Dashing, Attacking, Damaged, Dying
	}

	public enum Form {
		Demon, Angel
	}

	float baseDepth = HEIGHT * .13f; // base depth of torso

	// demonlegs
	BasicGameObject dlegs;
	AnimationController dlegsAnim;

	float angelSpeed = 270;
	float demonSpeed = 180;

	PlayerControl control;
	AttackControl attack;
	// attack listeners
	float basicProjectileCD = .35f;
	AttackListener basicProjectile;
	float basicMeleeCD = .75f;
	AttackListener basicMelee;

	AnimationListener attackAnimationListener = new AnimationListener() {
		@Override
		public void finished(String animation, int times) {
			if (times >= 1) {
				getAnimation().setListener(null);

				switch (getDirection()) {
				case Down:
					getAnimation().setAnimation(DEMONWALKDOWN);
					break;
				case Up:
					getAnimation().setAnimation(DEMONWALKUP);
					break;
				case Left:
				case Right:
					getAnimation().setAnimation(DEMONWALKSIDE);
					break;

				default:
					break;
				}

				System.out.println("times=" + times);
				setPlayerState(PlayerState.Idling);
			}
		}
	};

	String animation;
	Form form;
	PlayerState state;// will probably change this to its own state to make it easy to have states
						// such as "walking","attacking","damaged","dying", ect.

	public Player(int id) {
		// we set this control to the id of 0 which is corresponding of the
		// PlayerControls in PlayerControlSystem
		// We can support more than one player but must add the playerControls to the
		// PlayerControlSystem
		control = new PlayerControl(id);
		attack = new AttackControl(null);
		setName("Player@" + id);

		setupBasicProjectile();
		setUpBasicMelee();

		setCurrentForm(Form.Demon);
		setDirection(Direction.Down);

		getHealth().setHealthGroup(HealthGroup.Player);

		dlegs = new BasicGameObject();
		dlegs.setName(DEMON + LEGS + id);
		dlegsAnim = new AnimationController();

		setPreDrawChildren(true);
		setTransform(true);

	}

	public void setCurrentForm(Form form) {
		this.form = form;
		switch (form) {
		case Demon:

			getMove().setFlying(false);
			setDepth(baseDepth);
			setCollisionBounds(getBoundsRaw().x, getDepth(), getBoundsRaw().width, getBoundsRaw().height);
			getMove().setMoveSpeed(demonSpeed);
			attack.setAttackListener(basicMelee);
			attack.setCooldown(basicMeleeCD);

			break;
		case Angel:
			getMove().setFlying(true);
			setDepth(baseDepth + getBoundsRaw().height * .5f);
			setCollisionBounds(getBoundsRaw().x, getDepth(), getBoundsRaw().width, getBoundsRaw().height);
			getMove().setMoveSpeed(angelSpeed);
			attack.setAttackListener(basicProjectile);
			attack.setCooldown(basicProjectileCD);
			break;
		}
		if (KyperBoxGame.DEBUG_LOGGING)
			System.out.println(StringUtils.format("%s form Initiated", form.name()));
	}

	public Form getCurrentForm() {
		return form;
	}

	public void setPlayerState(PlayerState state) {

		if (this.state != state) {

			switch (state) {
			case Moving:
				dlegsAnim.setPlaySpeed(1f);
				break;
			case Idling:
				dlegsAnim.setPlaySpeed(0f);
				break;
			default:
				break;
			}

			this.state = state;

		}
	}

	public PlayerState getPlayerState() {
		return state;
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		createLegAnimations(getState());

		dlegs.addController(dlegsAnim);
		dlegs.setSize(WIDTH * .8f, HEIGHT * .4f);
		dlegs.setPosition(WIDTH * .1f, 0);
		addChild(dlegs);
		dlegsAnim.setAnimation(DEMONLEGS, PlayMode.LOOP);
		setSize(WIDTH, HEIGHT * .7f);

		setPlayerState(PlayerState.Idling);

		// we must add a controller in the init method since all controlers get removed
		// from objects when the objects are removed from the gamestate/gamelayer
		addController(control);
		addController(attack);

		// we can set the collision bounds of this object independent of its actual
		// size.
		// collision bounds is what is actually used by the collisionSystem which is a
		// QuadTree in our case
		setCollisionBounds(getWidth() * .2f, 0, getWidth() * .6f, getHeight() * .6f);

		// this isnt the best way to do it since init gets called each time the object
		// is loaded in but later we can move this
		// to a static method that loads it once per state load. Animations can be
		// reused between objects to save on space

		AnimationController animation = getAnimation();
		animation.addAnimation(DEMONWALKDOWN, DEMONWALKDOWN);
		animation.addAnimation(DEMONWALKUP, DEMONWALKUP);
		animation.addAnimation(DEMONWALKSIDE, DEMONWALKSIDE);
		animation.addAnimation(DEMONATTACKSIDE, DEMONATTACKSIDE);

		getHealth().setDamageListener(new DamageListener() {
			@Override
			public void damaged(float amount) {
				System.out.println(StringUtils.format("%s damaged - amount[%s] - currentHealth[%s]", getName(), amount,
						getHealth().getCurrentHealth()));
			}
		});

		getDirectionControl().setDirectionListener(new DirectionChangeListener() {
			@Override
			public void directionChanged(Direction lastDirection, Direction newDirection) {

				System.out.println("currentState = " + state.name() + " newdir=" + newDirection.name());
				if (state == PlayerState.Moving) {

					switch (newDirection) {
					case Down:
						getAnimation().set(DEMONWALKDOWN);
						// System.out.println("going down");
						dlegsAnim.setAnimation(DEMONLEGS, PlayMode.LOOP);
						dlegs.setFlip(false, false);
						break;
					case Up:
						getAnimation().set(DEMONWALKUP);
						// System.out.println("going up");
						dlegsAnim.setAnimation(DEMONLEGS, PlayMode.LOOP);
						dlegs.setFlip(true, false);
						break;
					case Left:

						getAnimation().set(DEMONWALKSIDE);

						setFlip(false, false);
						dlegs.setFlip(false, false);
						dlegsAnim.setAnimation(DEMONLEGSSIDE, PlayMode.LOOP);
						break;
					case Right:
						getAnimation().set(DEMONWALKSIDE);

						setFlip(true, false);
						dlegs.setFlip(true, false);
						dlegsAnim.setAnimation(DEMONLEGSSIDE, PlayMode.LOOP);
						break;
					}

				}

			}
		});

	}

	private void createLegAnimations(GameState state) {
		float speed = .14f;
		Animation<KyperSprite> anim = state.getAnimation(DEMONLEGS);
		if (anim == null)
			state.storeAnimation(DEMONLEGS, state.createGameAnimation(DEMONLEGS, speed));
		anim = state.getAnimation(DEMONLEGSSIDE);
		if (anim == null)
			state.storeAnimation(DEMONLEGSSIDE, state.createGameAnimation(DEMONLEGSSIDE, speed));
	}

	public static void createPlayerAnimations(GameState state) {
		float framespeed = .15f;
		Animation<KyperSprite> anim = state.getAnimation(DEMONWALKDOWN);
		if (anim == null) {
			state.storeAnimation(DEMONWALKDOWN, state.createGameAnimation(DEMONWALKDOWN, framespeed));
		}
		anim = state.getAnimation(DEMONWALKUP);
		if (anim == null) {
			state.storeAnimation(DEMONWALKUP, state.createGameAnimation(DEMONWALKUP, framespeed));
		}
		anim = state.getAnimation(DEMONWALKSIDE);
		if (anim == null) {
			state.storeAnimation(DEMONWALKSIDE, state.createGameAnimation(DEMONWALKSIDE, framespeed));
		}

		anim = state.getAnimation(DEMONATTACKSIDE);
		if (anim == null) {
			state.storeAnimation(DEMONATTACKSIDE, state.createGameAnimation(DEMONATTACKSIDE, framespeed));
		}
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		AnimationController animation = getAnimation();
		Vector2 vel = getVelocity();
		// TODO: for now it doesnt update animations if player is flying to tell it
		// apart from its grounded form . later we add some sort of shadow and gradually
		// increase the depth
		if (!getMove().isFlying())
			animation.setPlaySpeed(1f);
		else
			animation.setPlaySpeed(0f);

		// TODO: Question - would it make more sense to put door collisions here?
		// Or in the door object- just thinking doors only work with Players.

		Array<CollisionData> cols = getCollision().getCollisions();
		for (int i = 0; i < cols.size; i++) {
			CollisionData data = cols.get(i);
			GameObject target = data.getTarget();

			if (target instanceof Collectible) {
				Collectible c = (Collectible) target;
				if (!c.isCollected()) {

					// do something with the id of the item collected
					int itemID = c.getId();
					if (itemID == Collectible.KEY)
					{
						m_numKeys++;
						System.out.println("Keys + 1");

					}

					System.out.println(StringUtils.format("%s collected itemId[%s]", getName(), itemID));


					// collect the item so that it cannot be collected again
					c.collect();

				}

			}
		}

	}

	public void setAnimation(String animation) {
		if (this.animation == null || !this.animation.equals(animation)) {
			getAnimation().set(animation, PlayMode.LOOP);
			this.animation = animation;
		}
	}

	// melee attack

	private void setupMelee(MeleeAttack melee) {
		setMeleeBounds(melee);
		setMeleePos(melee);
	}

	private void setMeleeBounds(MeleeAttack melee) {
		switch (getDirection()) {
		case Up:
		case Down:
			melee.setSize(getHeight() * 2f, getHeight() * .75f);
			melee.setBounds(0, 0, getHeight() * 2f, getHeight() * .75f);
			break;
		case Left:
		case Right:
			melee.setSize(getHeight() * .75f, getHeight() * 2f);
			melee.setBounds(0, 0, getHeight() * .75f, getHeight() * 2f);
			break;
		}
	}

	private void setMeleePos(MeleeAttack melee) {
		Vector2 center = getCollisionCenter();
		switch (getDirection()) {
		case Up:
			melee.setPosition(center.x - melee.getWidth() * .5f, center.y + getBoundsRaw().height * .5f);
			break;
		case Down:
			melee.setPosition(center.x - melee.getWidth() * .5f, getY() + getBoundsRaw().y - melee.getHeight());
			break;
		case Left:
			melee.setPosition(center.x - getBoundsRaw().width * .5f - melee.getWidth(),
					center.y - melee.getHeight() * .5f);
			break;
		case Right:
			melee.setPosition(center.x + getBoundsRaw().width * .5f, center.y - melee.getHeight() * .5f);
			break;
		}
	}

	// attack listeners
	private void setupBasicProjectile() {

		basicProjectile = new AttackListener() {
			@Override
			public void onAttack() {

				if (form != null) {
					Projectile p = Projectile.get(); // get a pooled projectile
					p.setVelocity(0, 0);
					switch (getDirection()) {
					case Right:
						p.setPosition(getX() + getWidth(), getY() + getHeight() * .5f + getDepth());
						break;
					case Left:
						p.setPosition(getX(), getY() + getHeight() * .5f + getDepth());
						break;
					case Up:
						p.setPosition(getX() + getWidth() * .5f, getY() + getHeight() + getDepth());
						break;
					case Down:
						p.setPosition(getX() + getWidth() * .5f, getY() + getDepth());
						break;
					default:
						break;
					}

					getGameLayer().addGameObject(p, KyperBoxGame.NULL_PROPERTIES);

					MoveControl pmove = p.getMove();
					switch (getDirection()) {
					case Right:
						pmove.setDirection(1f, 0);
						break;
					case Left:
						pmove.setDirection(-1f, 0);
						break;
					case Up:
						pmove.setDirection(0, 1f);
						break;
					case Down:
						pmove.setDirection(0, -1f);
						break;
					default:
						break;
					}

				}

				if (KyperBoxGame.DEBUG_LOGGING)
					System.out.println(StringUtils.format("Attacked in %s form", form.name()));
			}
		};
	}

	public boolean useKey()
	{
		if (m_numKeys > 0)
		{
			m_numKeys--;
			return true;
		}
		return false;
	}

	private void setUpBasicMelee() {

		basicMelee = new AttackListener() {

			@Override
			public void onAttack() {

				switch (getDirection()) {
				case Down:
					//TODO: Add down attack animation
					setPlayerState(PlayerState.Idling);
					break;
				case Up:
					//TODO: Add up attack animation
					setPlayerState(PlayerState.Idling);
					break;
				case Left:
				case Right:
					getAnimation().set(DEMONATTACKSIDE);
					getAnimation().setListener(attackAnimationListener);
					break;

				default:
					break;
				}

				MeleeAttack m = MeleeAttack.get(HealthGroup.Angel, HealthGroup.Demon, HealthGroup.Neutral);

				setupMelee(m);

				getGameLayer().addGameObject(m, KyperBoxGame.NULL_PROPERTIES);

			}
		};
	}
}
