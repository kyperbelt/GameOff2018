package com.gameoff.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.gameoff.game.GameOffGame;
import com.gameoff.game.Sounds;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.AttackControl;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.AttackControl.AttackListener;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.DirectionControl.DirectionChangeListener;
import com.gameoff.game.control.HealthControl.DamageListener;
import com.gameoff.game.control.HealthControl.DeathListener;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.PlayerControl;
import com.gameoff.game.control.ZOrderControl;
import com.gameoff.game.objects.enemies.CherubEnemy;
import com.gameoff.game.objects.enemies.ContactDamage;
import com.gameoff.game.objects.enemies.ScorpionEnemy;
import com.gameoff.game.objects.enemies.SimpleEnemy;
import com.gameoff.game.objects.enemies.SpiderBossEnemy;
import com.gameoff.game.objects.enemies.WormEnemy;
import com.kyperbox.GameState;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.controllers.AnimationController.AnimationListener;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.BasicGameObject;
import com.kyperbox.objects.GameLayer.LayerCamera;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.KyperSprite;
import com.kyperbox.umisc.StringUtils;

public class Player extends DirectionEntity implements AnimationListener {

	boolean player_debug = false;

	public static float WIDTH = 144 * .5f;
	public static float HEIGHT = 160 * .5f;

	// animation literals start -->
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
	public static final String ANGELWALKUP = ANGEL + SEP + WALKUP;
	public static final String ANGELWALKSIDE = ANGEL + "_walk_" + SIDE;
	public static final String ANGELLEGS = ANGEL + SEP + LEGS;
	public static final String ANGELLEGSSIDE = ANGELLEGS + SIDE;
	public static final String ANGELATTACKSIDE = ANGEL + SEP + ATTACKSIDE;
	public static final String ANGELATTACKUP = ANGEL + SEP + ATTACKUP;
	public static final String ANGELATTACKDOWN = ANGEL + SEP + ATTACKDOWN;

	public static final String DEMONWALKDOWN = DEMON + SEP + WALKDOWN;
	public static final String DEMONWALKUP = DEMON + SEP + WALKUP;
	public static final String DEMONWALKSIDE = DEMON + "_walk_" + SIDE;
	public static final String DEMONLEGS = DEMON + SEP + LEGS;
	public static final String DEMONLEGSSIDE = DEMONLEGS + SIDE;
	public static final String DEMONATTACKSIDE = DEMON + SEP + ATTACKSIDE;
	public static final String DEMONATTACKUP = DEMON + SEP + ATTACKUP;
	public static final String DEMONATTACKDOWN = DEMON + SEP + ATTACKDOWN;
	public static final String TRANSFORM = "transform";

	public static final String HEADFLAMESTART = "headFlame_start";
	public static final String HEADFLAME = "headFlame";

	public static final String DEMONFLAME = "demonFlame";
	public static final String DEMONFLAMESIDE = "demonFlame_side";

	public static final String ANGELSHIELD = "angelShield";

	// -projectiles
	public static final String HALOPROJECTILEV = "halo_projectile_vertical";
	public static final String HALOPROJECTILEH = "halo_projectile_horizontal";

	// -- attacks
	public static final String MELEE_ATTACK = "slash";

	public static float MELEE_LIFE = .2f;
	public static int MELEE_FRAMES = 4;
	// damage stuff
	private static final float MINPUSHBACK = 20;
	private static final float MAXPUSHBACK = 100;
	private float damageDuration = 1.5f;
	private float damageElapsed = 0;
	private float m_damageMultiplier = 0f; // reduces damage
	private float m_weaponDamageMultiplier = 1f;

	private Action a_setIdling = new Action() {
		@Override
		public boolean act(float delta) {
			setPlayerState(PlayerState.Idling);
			return true;
		}
	};

	private Action a_removeInvulnerable = new Action() {
		@Override
		public boolean act(float delta) {
			if (isAngelShieldActive == false)
			{
					getHealth().setInvulnerable(false);
			}
			return true;
		}
	};

	private float pushbackAngle = 0f;

	// deathstuff
	private BasicGameObject square;
	private boolean dying = false;
	private float deathTime = 3f;
	private float deathElapsed = 0;

	public float projectileSpeed = 650;

	public boolean isDying() {
		return dying;
	}

	Form targetForm;
	// --animation literals end <--

	public int m_numKeys = 0;
	public int m_numSouls = 20;

	public float angelDrainRate = .5f; // .5 souls per second at 1 soul per 2 seconds
	public float angelShieldDrainRate = 1f; // 1 soul per second
	public float demonFlameDrainRate = 3f; // 1 soul per second

	public float angelFormMin = 1;
	public float soulDrainRate = 0f; // amount of souls drained per second
	public float soulDrainElapsed = 0f; // used to calculate the amount of soul drained into whole numbers

	public enum PlayerState {
		Idling, Moving, Dashing, Attacking, Damaged, Dying
	}

	public enum Form {
		Demon, Angel
	}

	float baseDepth = HEIGHT * .13f; // base depth of torso

	// headFlame
	BasicGameObject headFlame;
	ZOrderControl headFlameZorder;
	AnimationController headFlameAnim;

	// demonFlame
	BasicGameObject demonFlame;
	ZOrderControl demonFlameZOrder;
	AnimationController demonFlameAnim;
	BasicGameObject flameCheck;
	float flameRate = 1f; // burn enemies every second
	float flameElapsed = 0; // elapsed time till next burn
	float flameDamage = 1; // damage the flame does to enemies
	float flameRadius = 300; // the distance at which the flame is effective

	// angelShield
	BasicGameObject angelShield;
	ZOrderControl angelShieldZorder;
	AnimationController angelShieldAnim;

	// demonlegs
	BasicGameObject dlegs;
	ZOrderControl legsZOrder;
	AnimationController dlegsAnim;
	BasicGameObject transformSprite;
	AnimationController transformAnim;

	BasicGameObject playerShadow;
	float shadowOffset = 0;
	float legsOffsetX = 0;
	float legsOffsetY = 0;
	float legsDeltaX = 0;
	float legsDeltaXFactor = 0.1f;
	boolean legsBounceX = false;
	float legsDeltaY = 0;
	float legsOffsetXTarget = 0;
	float legsOffsetYTarget = 0;
	float legsX = 0;
	float legsY = 0;

	float lastXDir = 0;
	float lastYDir = 0;

	float angelSpeed = 330;
	float demonSpeed = 285;

	PlayerControl control;
	AttackControl attack;
	// attack listeners
	float basicProjectileCD = .2f;
	AttackListener basicProjectile;
	float basicMeleeCD = .4f;
	AttackListener basicMelee;
	MeleeAttack melee;

	float transformTime = 0;
	boolean transforming = false;

	boolean isDemonFlameActive = false;
	boolean isAngelShieldActive = false;

	AnimationListener attackAnimationListener = new AnimationListener() {
		@Override
		public void finished(String animation, int times) {
			if (times >= 1) {
				getAnimation().setListener(null);
				setWalkAnimation(getDirection(), getCurrentForm());

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

		headFlame = new BasicGameObject();
		headFlame.setName("headFlame");
		headFlameAnim = new AnimationController();
		headFlameZorder = new ZOrderControl();
		headFlameZorder.setZOrder(ZOrder.PLAYER - 1);

		demonFlame = new BasicGameObject();
		demonFlame.setName("demonFlame");
		demonFlameAnim = new AnimationController();
		demonFlameZOrder = new ZOrderControl();
		demonFlameZOrder.setZOrder(ZOrder.PLAYER + 1);
		flameCheck = new BasicGameObject();
		flameCheck.setSize(flameRadius * 2f, flameRadius * 2f);
		flameCheck.setCollisionBounds(0, 0, flameCheck.getWidth(), flameCheck.getHeight());

		angelShield = new BasicGameObject();
		angelShield.setName("angelShield");
		angelShieldAnim = new AnimationController();
		angelShieldZorder = new ZOrderControl();
		angelShieldZorder.setZOrder(ZOrder.PLAYER - 1);

		dlegs = new BasicGameObject();
		dlegs.setName(DEMON + LEGS + id);
		dlegsAnim = new AnimationController();
		legsZOrder = new ZOrderControl();
		legsZOrder.setZOrder(ZOrder.PLAYER);

		playerShadow = new BasicGameObject();
		playerShadow.setSprite("player_shadow");

		transformSprite = new BasicGameObject();
		transformSprite.setName("transform");
		transformAnim = new AnimationController();
		transformAnim.setListener(this);
		transformSprite.setVisible(false);

		setCurrentForm(Form.Demon);
		setDirection(Direction.Down);

		getHealth().setHealthGroup(HealthGroup.Player);

		square = new BasicGameObject();

		setTransform(true);

	}

	public void transformTo(Form form) {
		if (form == Form.Angel) {
			m_numSouls--;
		}
		transformSprite.setVisible(true);
		transformAnim.setAnimation(TRANSFORM, PlayMode.NORMAL);
		transformAnim.setPlayMode(PlayMode.NORMAL);
		setPreDrawChildren(false);
		transforming = true;
		transformTime = 0.25f;
		targetForm = form;
		control.setState(PlayerState.Idling);
		getMove().setDirection(0, 0);
		getMove().setMoveSpeed(0);
		setVelocity(0, 0);
	}

	public void setCurrentForm(Form form) {
		this.form = form;
		switch (form) {
		case Demon:
			soulDrainRate = Math.max(0f, soulDrainRate - angelDrainRate);
			getMove().setFlying(false);
			setDepth(baseDepth);
			setCollisionBounds(getBoundsRaw().x, getDepth(), getBoundsRaw().width, getBoundsRaw().height);
			getMove().setMoveSpeed(demonSpeed);
			attack.setAttackListener(basicMelee);
			attack.setCooldown(basicMeleeCD);
			dlegs.setVisible(true);
			dlegs.setDepth(0);
			// setPreDrawChildren(true);
			playerShadow.setVisible(false);
			float legsOffsetXTarget = 0;
			float legsOffsetX = 0;
			dlegs.setPosition(legsX, legsY);
			break;
		case Angel:
			soulDrainRate += angelDrainRate;
			getMove().setFlying(true);
			/// float depthDiff = (baseDepth + getBoundsRaw().height * .5f)-baseDepth;
			setDepth(baseDepth + getBoundsRaw().height * .5f);
			setCollisionBounds(getBoundsRaw().x, getDepth(), getBoundsRaw().width, getBoundsRaw().height);
			getMove().setMoveSpeed(angelSpeed);
			attack.setAttackListener(basicProjectile);
			attack.setCooldown(basicProjectileCD);
			dlegs.setDepth(getHeight() * .20f);
			dlegs.setVisible(true);
			// setPreDrawChildren(true);
			playerShadow.setVisible(true);
			break;
		}

		setWalkAnimation(getDirection(), form);
		if (KyperBoxGame.DEBUG_LOGGING)
			System.out.println(StringUtils.format("%s form Initiated", form.name()));
	}

	public void updateToCurrentForm() {
		setCurrentForm(this.form);
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

	@Override
	public void onRemove() {
		super.onRemove();
		addHeadFlame(false);
		addDemonFlame(false);
		setAngelShieldActive(false);
	}

	public PlayerState getPlayerState() {
		return state;
	}

	private void setWalkAnimation(Direction dir, Form form) {
		if (getGameLayer() == null)
			return;
		if (form == Form.Demon) {
			switch (dir) {
			case Down:
				getAnimation().setAnimation(DEMONWALKDOWN);
				dlegsAnim.setAnimation(DEMONLEGS, PlayMode.LOOP);
				dlegs.setFlip(false, false);
				break;
			case Up:
				getAnimation().setAnimation(DEMONWALKUP);
				dlegsAnim.setAnimation(DEMONLEGS, PlayMode.LOOP);
				dlegs.setFlip(true, false);
				break;
			case Left:
				getAnimation().setAnimation(DEMONWALKSIDE);
				setFlip(false, false);
				dlegs.setFlip(false, false);
				dlegsAnim.setAnimation(DEMONLEGSSIDE, PlayMode.LOOP);
				break;
			case Right:
				getAnimation().setAnimation(DEMONWALKSIDE);
				setFlip(true, false);
				dlegs.setFlip(true, false);
				dlegsAnim.setAnimation(DEMONLEGSSIDE, PlayMode.LOOP);
				break;

			default:
				break;
			}
		} else if (form == Form.Angel) {

			switch (dir) {
			case Down:
				getAnimation().setAnimation(ANGELWALKDOWN);
				dlegsAnim.setAnimation(ANGELLEGS, PlayMode.LOOP);
				dlegs.setFlip(false, false);
				shadowOffset = 2;
				break;
			case Up:
				getAnimation().setAnimation(ANGELWALKUP);
				dlegsAnim.setAnimation(ANGELLEGS, PlayMode.LOOP);
				dlegs.setFlip(true, false);
				shadowOffset = 0;
				break;
			case Left:
				getAnimation().setAnimation(ANGELWALKSIDE);
				setFlip(false, false);
				dlegs.setFlip(false, false);
				dlegsAnim.setAnimation(ANGELLEGSSIDE, PlayMode.LOOP);
				shadowOffset = 4;
				break;
			case Right:
				getAnimation().setAnimation(ANGELWALKSIDE);
				setFlip(true, false);
				dlegs.setFlip(true, false);
				dlegsAnim.setAnimation(ANGELLEGSSIDE, PlayMode.LOOP);
				shadowOffset = -5;
				break;

			default:
				break;
			}
			playerShadow.setPosition(20 + shadowOffset, -3);
		}
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		soulDrainRate = 0;
		playerShadow.setVisible(false);
		playerShadow.setPosition(20, -3);
		addChild(playerShadow);
		playerShadow.setSize(30, 20);

		createLegAnimations(getState());
		dlegs.addController(dlegsAnim);
		dlegs.addController(legsZOrder);
		dlegs.setSize(WIDTH * .8f, HEIGHT * .4f);
		legsX = WIDTH * .1f;
		legsY = 0;

		dlegs.setPosition(legsX, legsY);

		addChild(dlegs);
		dlegsAnim.setAnimation(DEMONLEGS, PlayMode.LOOP);
		setSize(WIDTH, HEIGHT * .7f);

		createTransformAnimations(getState());
		transformSprite.addController(transformAnim);
		transformSprite.setPosition(-135, -65);
		addChild(transformSprite);
		transformSprite.setSize(400, 225);
		transformSprite.setVisible(false);

		// headflame
		// addHeadFlame(true);

		// demonflame
		// addDemonFlame(true);

		// angelShield

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
		animation.addAnimation(DEMONATTACKDOWN, DEMONATTACKDOWN);
		animation.addAnimation(DEMONATTACKUP, DEMONATTACKUP);
		// --
		animation.addAnimation(ANGELWALKDOWN, ANGELWALKDOWN);
		animation.addAnimation(ANGELWALKUP, ANGELWALKUP);
		animation.addAnimation(ANGELWALKSIDE, ANGELWALKSIDE);
		animation.addAnimation(ANGELATTACKSIDE, ANGELATTACKSIDE);
		animation.addAnimation(ANGELATTACKDOWN, ANGELATTACKDOWN);
		animation.addAnimation(ANGELATTACKUP, ANGELATTACKUP);

		getHealth().setDamageListener(new DamageListener() {
			@Override
			public void damaged(float amount) {

				float dmg = amount * m_damageMultiplier;
				if (form == Form.Angel) {
					// take double damage when angel!
					getHealth().changeHealthNoListener(-amount);
					dmg = amount * m_damageMultiplier * 2;
				}

				if (player_debug)
					dmg = amount * 2;

				// add back health if you have the shield
				getHealth().changeHealthNoListener(dmg);

				// System.out.println("added dmg back: " + dmg);

				if (getHealth().shouldDie())
					return;

				getState().playSound(Sounds.PlayerDamaged);

				clearActions();
				getHealth().setInvulnerable(true);
				float dst = MathUtils.random(MINPUSHBACK, MAXPUSHBACK);
				float x = MathUtils.cos(pushbackAngle) * dst;
				float y = MathUtils.sin(pushbackAngle) * dst;

				int fr = 4;
				float frr = fr * 2f;

				getMove().setDirection(0, 0);
				setVelocity(0, 0);

				addAction(
						Actions.sequence(Actions.parallel(
								Actions.repeat(fr,
										Actions.sequence(Actions.fadeOut(damageDuration / frr),
												Actions.fadeIn(damageDuration / frr))),
								Actions.sequence(
										Actions.moveTo(getX() + x, getY() + y,
												(dst / MAXPUSHBACK) * (damageDuration * .3f), Interpolation.linear),
										a_setIdling)),
								Actions.alpha(1f), a_removeInvulnerable));

				setPlayerState(PlayerState.Damaged);
			}
		});

		getHealth().setDeathListener(new DeathListener() {
			@Override
			public boolean die(float delta) {
				if (!dying) {
					dying = true;
					deathElapsed = 0;
					setPlayerState(PlayerState.Dying);
					getGameLayer().addGameObject(square, GameOffGame.NULL_PROPERTIES);
					square.clearActions();
					square.addAction(Actions.fadeIn(deathTime * .33f));
					getZOrder().setZOrder(ZOrder.FOREGROUND - 10);
					getMove().setDirection(0, 0);
					setVelocity(0, 0);
					setPlayerState(PlayerState.Dying);
					clearActions();
					addAction(Actions.sequence(Actions.delay(deathTime * .66f), Actions.fadeOut(deathTime * .33f)));
					// getState().getSoundManager().stopSounds();
					// getHealth().setInvulnerable(true);
				}
				return deathElapsed >= deathTime;
			}
		});

		getDirectionControl().setDirectionListener(new DirectionChangeListener() {
			@Override
			public void directionChanged(Direction lastDirection, Direction newDirection) {

				if (!demonFlame.isRemoved())
					setDemonFlameDir(newDirection);
				// System.out.println("currentState = " + state.name() + " newdir=" +
				// newDirection.name());
				if (state == PlayerState.Moving) {

					setWalkAnimation(newDirection, getCurrentForm());

				}

			}
		});

		Viewport view = getState().getGame().getView();

		square.setSize(view.getWorldWidth() * 2, view.getWorldHeight() * 2);
		square.setSprite("square");
		ZOrderControl z = new ZOrderControl();
		z.setZOrder(ZOrder.FOREGROUND - 2);
		square.addController(z);
		square.setColor(Color.BLACK);
		square.getColor().a = 0f;

	}

	public void addHeadFlame(boolean add) {
		if (add) {

			Sprite s = getState().getGameSprite("headFlame_0");

			headFlame.setSize(s.getWidth() * .5f, s.getHeight() * .5f);

			headFlameAnim.setAnimation(HEADFLAMESTART, PlayMode.NORMAL);
			headFlameAnim.setListener(new AnimationListener() {
				@Override
				public void finished(String animation, int times) {
					if (times == 1) {

						headFlameAnim.setListener(null);
						headFlameAnim.setAnimation(HEADFLAME, PlayMode.LOOP);
					}
				}
			});

			headFlame.addController(headFlameAnim);
			headFlame.addController(headFlameZorder);

			Vector2 center = getCollisionCenter();
			headFlame.setPosition(getWidth() * .5f - headFlame.getWidth() * .5f, getHeight() * .95f);

			addChild(headFlame);
		} else {
			headFlame.removeController(headFlameAnim);
			headFlame.removeController(headFlameZorder);
			headFlame.remove();
		}
	}

	private void createLegAnimations(GameState state) {
		float speed = .14f;
		Animation<KyperSprite> anim = state.getAnimation(DEMONLEGS);
		if (anim == null)
			state.storeAnimation(DEMONLEGS, state.createGameAnimation(DEMONLEGS, speed));
		anim = state.getAnimation(DEMONLEGSSIDE);
		if (anim == null)
			state.storeAnimation(DEMONLEGSSIDE, state.createGameAnimation(DEMONLEGSSIDE, speed));
		anim = state.getAnimation(ANGELLEGS);
		if (anim == null)
			state.storeAnimation(ANGELLEGS, state.createGameAnimation(ANGELLEGS, speed));
		anim = state.getAnimation(ANGELLEGSSIDE);
		if (anim == null)
			state.storeAnimation(ANGELLEGSSIDE, state.createGameAnimation(ANGELLEGSSIDE, speed));
	}

	private void createTransformAnimations(GameState state) {
		Animation<KyperSprite> anim = state.getAnimation(TRANSFORM);
		if (anim == null) {
			state.storeAnimation(TRANSFORM, state.createGameAnimation(TRANSFORM, 0.05f));
		}
	}

	public static void createPlayerAnimations(GameState state) {
		float framespeed = .15f;

		// demon animations
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

		anim = state.getAnimation(DEMONATTACKUP);
		if (anim == null)
			state.storeAnimation(DEMONATTACKUP, state.createGameAnimation(DEMONATTACKUP, framespeed));

		anim = state.getAnimation(DEMONATTACKDOWN);
		if (anim == null)
			state.storeAnimation(DEMONATTACKDOWN, state.createGameAnimation(DEMONATTACKDOWN, framespeed));

		// angel animations

		anim = state.getAnimation(ANGELWALKDOWN);
		if (anim == null)
			state.storeAnimation(ANGELWALKDOWN, state.createGameAnimation(ANGELWALKDOWN, framespeed));

		anim = state.getAnimation(ANGELWALKUP);
		if (anim == null)
			state.storeAnimation(ANGELWALKUP, state.createGameAnimation(ANGELWALKUP, framespeed));

		anim = state.getAnimation(ANGELWALKSIDE);
		if (anim == null)
			state.storeAnimation(ANGELWALKSIDE, state.createGameAnimation(ANGELWALKSIDE, framespeed));

		anim = state.getAnimation(ANGELATTACKSIDE);
		if (anim == null)
			state.storeAnimation(ANGELATTACKSIDE, state.createGameAnimation(ANGELATTACKSIDE, framespeed));

		anim = state.getAnimation(ANGELATTACKUP);
		if (anim == null)
			state.storeAnimation(ANGELATTACKUP, state.createGameAnimation(ANGELATTACKUP, framespeed));

		anim = state.getAnimation(ANGELATTACKDOWN);
		if (anim == null)
			state.storeAnimation(ANGELATTACKDOWN, state.createGameAnimation(ANGELATTACKDOWN, framespeed));

		// --player projectiles
		anim = state.getAnimation(HALOPROJECTILEV);
		if (anim == null)
			state.storeAnimation(HALOPROJECTILEV, state.createGameAnimation(HALOPROJECTILEV, .0333f));

		anim = state.getAnimation(HALOPROJECTILEH);
		if (anim == null)
			state.storeAnimation(HALOPROJECTILEH, state.createGameAnimation(HALOPROJECTILEH, .0333f));

		anim = state.getAnimation(MELEE_ATTACK);
		if (anim == null)
			state.storeAnimation(MELEE_ATTACK, state.createGameAnimation(MELEE_ATTACK, MELEE_LIFE / MELEE_FRAMES));

		anim = state.getAnimation(DEMONFLAMESIDE);
		if (anim == null)
			state.storeAnimation(DEMONFLAMESIDE, state.createGameAnimation(DEMONFLAMESIDE, .16f));

		anim = state.getAnimation(DEMONFLAME);
		if (anim == null)
			state.storeAnimation(DEMONFLAME, state.createGameAnimation(DEMONFLAME, .16f));

		anim = state.getAnimation(ANGELSHIELD);
		if (anim == null)
			state.storeAnimation(ANGELSHIELD, state.createGameAnimation(ANGELSHIELD, .16f));

		anim = state.getAnimation(HEADFLAMESTART);
		if (anim == null)
			state.storeAnimation(HEADFLAMESTART, state.createGameAnimation(HEADFLAMESTART, .16f));

		anim = state.getAnimation(HEADFLAME);
		if (anim == null)
			state.storeAnimation(HEADFLAME, state.createGameAnimation(HEADFLAME, .16f));

	}

	public void showMessage(String msg, float duration) {
		BasicGameObject o = new BasicGameObject();
		Sprite mo = getState().getGameSprite(msg);
		o.setSprite(msg);
		o.setSize(mo.getWidth(), mo.getHeight());
		o.setPosition((960 - mo.getWidth()) / 2, 430);
		getState().getForegroundLayer().addGameObject(o, KyperBoxGame.NULL_PROPERTIES);
		o.clearActions();
		o.addAction(Actions.sequence(Actions.parallel(Actions.moveBy(0, 30, duration), Actions.fadeOut(duration)),
				Actions.removeActor()));
	}

	@Override
	public void update(float delta) {
		super.update(delta);

		if (dying) {
			LayerCamera cam = getGameLayer().getCamera();
			Vector2 p = new Vector2(0, square.getHeight() * .5f);
			p = cam.unproject(p);
			square.setPosition(p.x, p.y);
			deathElapsed += delta;
		} else {

			if (transforming) {
				transformTime -= delta;
				if (transformTime < 0) {
					setCurrentForm(targetForm);
					transformTime = 0.75f;
				}
				// getMove().setDirection(0, 0);
				return;
			}
			// AnimationController animation = getAnimation();
			// Vector2 vel = getVelocity();
			// // TODO: for now it doesnt update animations if player is flying to tell it
			// // apart from its grounded form . later we add some sort of shadow and
			// gradually
			// // increase the depth
			// if (!getMove().isFlying())
			// animation.setPlaySpeed(1f);
			// else
			// animation.setPlaySpeed(0f);

			// TODO: Question - would it make more sense to put door collisions here?
			// Or in the door object- just thinking doors only work with Players.

			Array<CollisionData> cols = getCollision().getCollisions();
			for (int i = 0; i < cols.size; i++) {
				CollisionData data = cols.get(i);
				GameObject target = data.getTarget();

				if (target instanceof Collectible) {
					Collectible c = (Collectible) target;
					if (!c.isCollected() && c.isCollectible()) {

						// do something with the id of the item collected
						int itemID = c.getId();
						if (itemID == Collectible.KEY) {
							m_numKeys++;
							System.out.println("Keys + 1");
						} else if (itemID == Collectible.HEART) {
							getHealth().changeCurrentHealth(3);
							showMessage("healthmessage", 2f);
							System.out.println("Health + 3");
						} else if (itemID == Collectible.SHIELD) {
							activateHalfDamage();
							showMessage("defensemessage", 4f);
							System.out.println("SHIELD got!");
						} else if (itemID == Collectible.SWORD) {
							activateWeaponDoubleDamage();
							showMessage("attackmessage", 4f);
							System.out.println("SWORD got!");
						} else if (itemID == Collectible.SOUL) {
							m_numSouls++;
							System.out.println("Souls + 1");
							getState().playSound(Sounds.PickupSoul);
						}

						System.out.println(StringUtils.format("%s collected itemId[%s]", getName(), itemID));

						// collect the item so that it cannot be collected again
						c.collect();

					}
				} else if (this.form != Form.Angel) {
					// instant death!
					if (target instanceof Pit) {
						if (target.getCollisionBounds().contains(getCollisionBounds())) {
							// TODO:Sound
							getHealth().changeCurrentHealth(-100);
						}
					}
				}
			}

			// handle soul drain
			{
				if (isSoulDraining()) {
					//System.out.println(StringUtils.format("draining %s souls per second", soulDrainRate));
					soulDrainElapsed += delta;
					float soulDrained = soulDrainElapsed * soulDrainRate;
					if (soulDrained >= 1) {
						soulDrainElapsed -= 1;
						if (!useSoul(1)) {

							if (isAngelShieldActive)
								setAngelShieldActive(false);

							if (isDemonFlameActive)
								setDemonFlameActive(false);

							if (getCurrentForm() == Form.Angel) {
								setAngelShieldActive(false);
								transformTo(Form.Demon);
							}
							soulDrainElapsed = 0;
						}

					}
				} else {
					soulDrainElapsed = 0f;
				}
			}

			// Handle robe physics -- and angel stuff
			if (this.form == Form.Angel) {

				if (isDemonFlameActive)
					setDemonFlameActive(false);

				MoveControl move = getMove();
				float cxd = move.getXDir();
				if (lastXDir != cxd) {
					if (cxd == 0) {
						legsOffsetXTarget = 0;
						legsDeltaX = -lastXDir * 0.1f;
						legsDeltaXFactor = 0.05f;
					} else {
						legsOffsetXTarget = -5 * cxd;
						legsDeltaX = -cxd * 0.2f;
						legsDeltaXFactor = 0.05f;
					}
					legsBounceX = false;
					lastXDir = cxd;
				}

				legsOffsetX += legsDeltaX;
				if (legsOffsetXTarget >= 0) {
					if (legsOffsetX > legsOffsetXTarget) {
						if (legsBounceX == false) {
							legsBounceX = true;
							legsOffsetXTarget -= 1;
							if (legsOffsetXTarget < 1) {
								legsOffsetXTarget = 1;
								legsOffsetX = legsOffsetXTarget;
								legsDeltaXFactor = 0;
								legsDeltaX = 0;
							}
						}
						legsDeltaX -= legsDeltaXFactor;
					} else {
						legsBounceX = false;
						legsDeltaX += legsDeltaXFactor;
					}
				} else if (legsOffsetXTarget < 0) {
					if (legsOffsetX < legsOffsetXTarget) {
						if (legsBounceX == false) {
							legsBounceX = true;
							legsOffsetXTarget += 1;
							if (legsOffsetXTarget > -1) {
								legsOffsetXTarget = -1;
								legsOffsetX = legsOffsetXTarget;
								legsDeltaXFactor = 0;
								legsDeltaX = 0;
							}
						}
						legsDeltaX += legsDeltaXFactor;
					} else {
						legsBounceX = false;
						legsDeltaX -= legsDeltaXFactor;
					}
				}
				if (legsDeltaX < -0.3f)
					legsDeltaX = -0.3f;
				if (legsDeltaX > 0.3f)
					legsDeltaX = 0.3f;

				dlegs.setPosition(legsX + legsOffsetX, legsY + legsOffsetY);
				playerShadow.setPosition(20 + shadowOffset + legsOffsetX, legsOffsetY - 3);

				if (!angelShield.isRemoved()) {
					Vector2 center = getCollisionCenter();
					angelShield.setPosition(center.x - angelShield.getWidth() * .5f,
							center.y - angelShield.getHeight() * .4f);

					angelShield.getColor().a = getColor().a;
				}
			} else {
				Vector2 center = getCollisionCenter();

				if (isAngelShieldActive)
					setAngelShieldActive(false);

				if (!demonFlame.isRemoved()) {
					demonFlame.setPosition(getX() + getWidth() * .5f - demonFlame.getWidth() * .5f,
							getY() - getHeight() * .5f);
					demonFlame.getColor().a = getColor().a;

					flameCheck.setPosition(center.x - flameCheck.getWidth() * .5f,
							center.y - flameCheck.getHeight() * .5f);

					flameElapsed += delta;

					if (flameElapsed >= flameRate) {
						flameElapsed = 0;
						Array<CollisionData> cd = getCollision().getCollisions(flameCheck, 0);
						
						System.out.println("searching For Target");
						for (int i = 0; i < cd.size; i++) {

							GameObject target = cd.get(i).getTarget();
							if(target == this)
								continue;
							
							float dst = center.dst(target.getCollisionCenter());
							System.out.println("targetFound");
							if (dst < flameRadius) {
								HealthControl thealth = target.getController(HealthControl.class);
								if(thealth!=null) {
									thealth.changeCurrentHealth(-flameDamage);
								}
							}

						}
					}

				}
			}
		}

	}

	public boolean isSoulDraining() {
		return soulDrainRate > 0f;
	}

	public void addAngelShield(boolean add) {
		if (add) {

			Sprite s = getState().getGameSprite("angelShield_0");

			angelShield.setSize(getWidth() * 1.7f, getHeight() * 2.2f);

			angelShieldAnim.setAnimation(ANGELSHIELD, PlayMode.LOOP);

			angelShield.addController(angelShieldAnim);
			angelShield.addController(angelShieldZorder);

			Vector2 center = getCollisionCenter();
			angelShield.setPosition(center.x - angelShield.getWidth() * .5f, center.y);

			getGameLayer().addGameObject(angelShield, GameOffGame.NULL_PROPERTIES);
		} else {
			angelShield.removeController(angelShieldAnim);
			angelShield.removeController(angelShieldZorder);
			angelShield.remove();
		}
	}

	/**
	 * add the surrounding demon flame to the player
	 */
	public void addDemonFlame(boolean add) {
		if (add) {

			Sprite s = getState().getGameSprite("demonFlame_0");

			demonFlame.setSize(s.getRegionWidth() * .6f, s.getRegionHeight() * .6f);

			demonFlame.addController(demonFlameAnim);
			demonFlame.addController(demonFlameZOrder);

			setDemonFlameDir(getDirection());

			Vector2 center = getCollisionCenter();
			demonFlame.setPosition(getX() + getWidth() * .5f - demonFlame.getWidth() * .5f, getY() - getHeight() * .5f);

			getGameLayer().addGameObject(demonFlame, GameOffGame.NULL_PROPERTIES);
		} else {
			demonFlame.removeController(demonFlameAnim);
			demonFlame.removeController(demonFlameZOrder);
			demonFlame.remove();
		}
	}

	public void setDemonFlameDir(Direction dir) {

		switch (dir) {
		case Up:
			demonFlame.setFlip(getFlipX(), false);
			demonFlameAnim.setAnimation(DEMONFLAME, PlayMode.LOOP);
			break;
		case Down:
			demonFlame.setFlip(getFlipX(), false);
			demonFlameAnim.setAnimation(DEMONFLAME, PlayMode.LOOP);
			break;
		case Left:
			demonFlameAnim.setAnimation(DEMONFLAMESIDE, PlayMode.LOOP);
			demonFlame.setFlip(false, false);
			break;
		case Right:
			demonFlameAnim.setAnimation(DEMONFLAMESIDE, PlayMode.LOOP);
			demonFlame.setFlip(true, false);
			break;
		}
	}

	public void setAnimation(String animation) {
		if (this.animation == null || !this.animation.equals(animation)) {
			getAnimation().set(animation, PlayMode.LOOP);
			this.animation = animation;
		}
	}

	public void activateHalfDamage() {
		// picked up shield, now you take half damage when hit
		m_damageMultiplier = 0.5f;
	}

	public void activateWeaponDoubleDamage() {
		m_weaponDamageMultiplier = 2f;
		if (melee != null)
			melee.setDamage(m_weaponDamageMultiplier * 2);
	}

	// melee attack

	private void setupMelee(MeleeAttack melee) {
		setMeleeBounds(melee);
		setMeleePos(melee);
	}

	private void setMeleeBounds(MeleeAttack melee) {

		melee.setSize(getHeight() * .75f, getHeight() * 2f);
		melee.setBounds(0, 0, getHeight() * .75f, getHeight() * 2f);
		melee.setFlip(false, false);
		melee.setOrigin(Align.center);

		switch (getDirection()) {
		case Up:
			melee.rotateBy(90);
			melee.setFlip(false, getFlipX());
			break;
		case Down:
			melee.rotateBy(-90);
			melee.setFlip(false, getFlipX());
			break;
		case Left:
			melee.setFlip(true, true);
			break;
		case Right:
			melee.setFlip(false, true);
			break;
		}
	}

	private void setMeleePos(MeleeAttack melee) {
		float d = Gdx.graphics.getDeltaTime();
		Vector2 center = getCollisionCenter();
		switch (getDirection()) {
		case Up:
			melee.setPosition(center.x - melee.getWidth() * .5f, center.y + getVelocity().y * .09f);
			break;
		case Down:
			melee.setPosition(center.x - melee.getWidth() * .5f,
					center.y - (-getVelocity().y * .09f) - (melee.getHeight() * .95f));
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

	long lastShoot = -1;

	// attack listeners
	private void setupBasicProjectile() {

		basicProjectile = new AttackListener() {
			@Override
			public void onAttack() {

				if (lastShoot != -1)
					getState().stopSound(Sounds.AngelShoot, lastShoot);
				lastShoot = getState().playSound(Sounds.AngelShoot);

				switch (getDirection()) {
				case Down:
					// TODO: Add down attack animation
					getAnimation().set(ANGELATTACKDOWN);
					getAnimation().setListener(attackAnimationListener);
					break;
				case Up:
					// TODO: Add up attack animation
					getAnimation().set(ANGELATTACKUP);
					getAnimation().setListener(attackAnimationListener);
					break;
				case Left:
				case Right:
					getAnimation().set(ANGELATTACKSIDE);
					getAnimation().setListener(attackAnimationListener);
					break;

				default:
					break;
				}

				if (form != null) {
					Projectile p = Projectile.get(HealthGroup.Angel, HealthGroup.Demon, HealthGroup.Neutral,
							HealthGroup.Boss); // get a
					// pooled
					// projectile
					p.setVelocity(0, 0);
					p.setRotation(0);
					p.setDamage(1 * m_weaponDamageMultiplier);
					float w = 0;
					float h = 0;

					switch (getDirection()) {
					case Right:
						p.setPosition(getX() + getWidth() * 0.3f, getY() + getHeight() * .75f + getDepth());
						p.getAnimation().setAnimation(HALOPROJECTILEH, PlayMode.NORMAL);
						p.setFlip(false, false);
						p.setSize(63, 9);
						w = p.getWidth();
						h = p.getHeight();
						p.setBounds(w * 0.3f, h * 0.15f, w * 0.3f, h * 0.6f);
						break;
					case Left:
						p.setPosition(getX() + getWidth() * 0.1f, getY() + getHeight() * .75f + getDepth());
						p.getAnimation().setAnimation(HALOPROJECTILEH, PlayMode.NORMAL);
						p.setFlip(true, false);
						p.setSize(63, 9);
						w = p.getWidth();
						h = p.getHeight();
						p.setBounds(w * 0.5f, h * 0.15f, w * 0.3f, h * 0.6f);
						break;
					case Up:
						p.setSize(52, 46);
						p.setPosition(getX() + getWidth() * .5f - (p.getWidth() * .5f),
								getY() + getHeight() + getDepth() - p.getHeight());
						p.getAnimation().setAnimation(HALOPROJECTILEV, PlayMode.NORMAL);
						p.setFlip(false, false);
						w = p.getWidth();
						h = p.getHeight();
						p.setBounds(w * 0.2f, h * 0.3f, w * 0.6f, h * 0.4f);
						break;
					case Down:
						p.setSize(52, 46);
						p.setPosition(getX() + getWidth() * .5f - (p.getWidth() * .5f),
								getY() + getDepth() + p.getHeight());
						p.getAnimation().setAnimation(HALOPROJECTILEV, PlayMode.NORMAL);
						p.setFlip(false, true);
						w = p.getWidth();
						h = p.getHeight();
						p.setBounds(w * 0.2f, h * 0.3f, w * 0.6f, h * 0.4f);
						break;
					default:
						break;
					}

					getGameLayer().addGameObject(p, KyperBoxGame.NULL_PROPERTIES);

					MoveControl pmove = p.getMove();

					pmove.setMoveSpeed(projectileSpeed);

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

	public boolean useKey() {
		if (m_numKeys > 0) {
			m_numKeys--;
			return true;
		}
		return false;
	}

	/**
	 * try and use the specified amount of souls- returns false if not enough souls
	 * 
	 * @param amount
	 * @return
	 */
	public boolean useSoul(int amount) {
		if (m_numSouls - amount >= 0) {
			m_numSouls -= amount;
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
					// TODO: Add down attack animation
					getAnimation().set(DEMONATTACKDOWN);
					getAnimation().setListener(attackAnimationListener);
					break;
				case Up:
					// TODO: Add up attack animation
					getAnimation().set(DEMONATTACKUP);
					getAnimation().setListener(attackAnimationListener);
					break;
				case Left:
				case Right:
					getAnimation().set(DEMONATTACKSIDE);
					getAnimation().setListener(attackAnimationListener);
					break;

				default:
					break;
				}

				melee = MeleeAttack.get(HealthGroup.Angel, HealthGroup.Demon, HealthGroup.Neutral, HealthGroup.Boss);
				melee.lifetime = MELEE_LIFE;
				melee.getAnimation().setAnimation(MELEE_ATTACK, PlayMode.NORMAL);
				setupMelee(melee);
				melee.setDamage(m_weaponDamageMultiplier * 2);

				
				
				getState().playSound(Sounds.Slash);
				getGameLayer().addGameObject(melee, KyperBoxGame.NULL_PROPERTIES);

			}
		};
	}

	public void collidedWith(Basic object) {

		float dx = object.getCollisionCenter().x - getCollisionCenter().x;
		float dy = object.getCollisionCenter().y - getCollisionCenter().y;
		pushbackAngle = MathUtils.atan2(dy, dx) - MathUtils.PI;

		if (player_debug)
			return;

		if (object instanceof SimpleEnemy) {
			getHealth().changeCurrentHealth(-ContactDamage.SIMPLE);
		} else if (object instanceof WormEnemy) {
			getHealth().changeCurrentHealth(-ContactDamage.WORM);
		} else if (object instanceof CherubEnemy) {
			getHealth().changeCurrentHealth(-ContactDamage.CHERUB);
		} else if (object instanceof ScorpionEnemy) {
			getHealth().changeCurrentHealth(-ContactDamage.SCORPION);
		} else if (object instanceof SpiderBossEnemy) {
			getHealth().changeCurrentHealth(-ContactDamage.BOSS);
		}
	}

	/**
	 * returns true if the method was successful in activating or deactivating the
	 * angel shield
	 * 
	 * @param activate
	 * @return
	 */
	public boolean setAngelShieldActive(boolean activate) {
		if (activate) {
			if (getCurrentForm() != Form.Angel)
				return false;
			if (m_numSouls >= 1) {
				isAngelShieldActive = true;
				soulDrainRate += angelShieldDrainRate;
				addAngelShield(true);
				getHealth().setInvulnerable(true);
				return true;
			}
			return false;
		} else {
			isAngelShieldActive = false;
			soulDrainRate = Math.max(0f, soulDrainRate - angelShieldDrainRate);
			addAngelShield(false);
			getHealth().setInvulnerable(false);
		}

		return true;
	}

	long fireSound = 0L;
	public boolean setDemonFlameActive(boolean activate) {
		if (activate) {
			if (getCurrentForm() != Form.Demon)
				return false;
			if (m_numSouls >= 1) {
				isDemonFlameActive = true;
				soulDrainRate += demonFlameDrainRate;
				addDemonFlame(true);
				flameElapsed = 0;
				fireSound = getState().playSound(Sounds.Fire, true);
				return true;
			}
			return false;
		} else {
			getState().stopSound(Sounds.Fire,fireSound);
			isDemonFlameActive = false;
			soulDrainRate = Math.max(0f, soulDrainRate - demonFlameDrainRate);
			addDemonFlame(false);
		}

		return true;
	}

	public boolean isAngelShieldActive() {
		return isAngelShieldActive;
	}

	public boolean isDemonFlameActive() {
		return isDemonFlameActive;
	}

	public boolean isTransforming() {
		return transforming;
	}

	// animation listener
	public void finished(String name, int playTime) {
		if (name == TRANSFORM) {
			if (transforming == false)
				return;
			transformSprite.setVisible(false);
			setPreDrawChildren(true);
			transforming = false;
			transformTime = 0;
			getMove().setDirection(0, 0);
			setVelocity(0, 0);
			// System.out.println("Finished");
		}
	}
}
