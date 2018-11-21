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
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.ZOrderControl;

public class Player extends DirectionEntity implements AnimationListener {

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
	public static final String ANGELATTACKSIDE = ANGEL+SEP+ATTACKSIDE;
	public static final String ANGELATTACKUP = ANGEL+SEP+ATTACKUP;
	public static final String ANGELATTACKDOWN = ANGEL+SEP+ATTACKDOWN;

	public static final String DEMONWALKDOWN = DEMON + SEP + WALKDOWN;
	public static final String DEMONWALKUP = DEMON + SEP + WALKUP;
	public static final String DEMONWALKSIDE = DEMON + "_walk_" + SIDE;
	public static final String DEMONLEGS = DEMON + SEP + LEGS;
	public static final String DEMONLEGSSIDE = DEMONLEGS + SIDE;
	public static final String DEMONATTACKSIDE = DEMON + SEP + ATTACKSIDE;
	public static final String DEMONATTACKUP = DEMON+SEP+ATTACKUP;
	public static final String DEMONATTACKDOWN = DEMON+SEP+ATTACKDOWN;
	public static final String TRANSFORM = "transform";
	
	//-projectiles
	public static final String HALOPROJECTILEV = "halo_projectile_vertical";
	public static final String HALOPROJECTILEH = "halo_projectile_horizontal";

	public float projectileSpeed = 600;
	
	Form targetForm;
	//--animation literals end <--

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

	float angelSpeed = 270;
	float demonSpeed = 180;

	PlayerControl control;
	AttackControl attack;
	// attack listeners
	float basicProjectileCD = .35f;
	AttackListener basicProjectile;
	float basicMeleeCD = .75f;
	AttackListener basicMelee;

	float transformTime = 0;
	boolean transforming = false;

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


		setTransform(true);

	}

	public void transformTo(Form form)
	{
		transformSprite.setVisible(true);
		transformAnim.setAnimation(TRANSFORM, PlayMode.NORMAL);
		transformAnim.setPlayMode(PlayMode.NORMAL);
		setPreDrawChildren(false);
		transforming = true;
		transformTime = 0.25f;
		targetForm = form;
		control.setState(PlayerState.Idling);
		getMove().setDirection(0,0);
		getMove().setMoveSpeed(0);
		setVelocity(0,0);
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
			dlegs.setVisible(true);
			dlegs.setDepth(0);
			//setPreDrawChildren(true);
			playerShadow.setVisible(false);
			float legsOffsetXTarget = 0;
			float legsOffsetX = 0;
			dlegs.setPosition(legsX, legsY);
			break;
		case Angel:
			getMove().setFlying(true);
			///float depthDiff = (baseDepth + getBoundsRaw().height * .5f)-baseDepth;
			setDepth(baseDepth + getBoundsRaw().height * .5f);
			setCollisionBounds(getBoundsRaw().x, getDepth(), getBoundsRaw().width, getBoundsRaw().height);
			getMove().setMoveSpeed(angelSpeed);
			attack.setAttackListener(basicProjectile);
			attack.setCooldown(basicProjectileCD);
			dlegs.setDepth(getHeight()*.20f);
			dlegs.setVisible(true);
			//setPreDrawChildren(true);
			playerShadow.setVisible(true);
			break;
		}
		
		setWalkAnimation(getDirection(), form);
		if (KyperBoxGame.DEBUG_LOGGING)
			System.out.println(StringUtils.format("%s form Initiated", form.name()));
	}

	public void updateToCurrentForm()
	{
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

	public PlayerState getPlayerState() {
		return state;
	}
	
	private void setWalkAnimation(Direction dir ,Form form) {
		if(getGameLayer() == null)
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
			playerShadow.setPosition(20+shadowOffset, -3);
		}
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		playerShadow.setVisible(false);
		playerShadow.setPosition(20, -3);
		addChild(playerShadow);
		playerShadow.setSize(30,20);

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
		transformSprite.setPosition(-135,-65);
		addChild(transformSprite);
		transformSprite.setSize(400,225);
		transformSprite.setVisible(false);


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
		//--
		animation.addAnimation(ANGELWALKDOWN, ANGELWALKDOWN);
		animation.addAnimation(ANGELWALKUP, ANGELWALKUP);
		animation.addAnimation(ANGELWALKSIDE, ANGELWALKSIDE);
		animation.addAnimation(ANGELATTACKSIDE, ANGELATTACKSIDE);
		animation.addAnimation(ANGELATTACKDOWN, ANGELATTACKDOWN);
		animation.addAnimation(ANGELATTACKUP, ANGELATTACKUP);

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

				//System.out.println("currentState = " + state.name() + " newdir=" + newDirection.name());
				if (state == PlayerState.Moving) {

					setWalkAnimation(newDirection, getCurrentForm());

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
		anim = state.getAnimation(ANGELLEGS);
		if(anim == null)
			state.storeAnimation(ANGELLEGS, state.createGameAnimation(ANGELLEGS, speed));
		anim = state.getAnimation(ANGELLEGSSIDE);
		if(anim == null)
			state.storeAnimation(ANGELLEGSSIDE, state.createGameAnimation(ANGELLEGSSIDE, speed));
	}

	private void createTransformAnimations(GameState state)
	{
		Animation<KyperSprite> anim = state.getAnimation(TRANSFORM);
		if (anim == null) {
			state.storeAnimation(TRANSFORM, state.createGameAnimation(TRANSFORM, 0.05f));
		}
	}
		

	public static void createPlayerAnimations(GameState state) {
		float framespeed = .15f;
		
		//demon animations
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
		if(anim == null)
			state.storeAnimation(DEMONATTACKUP,state.createGameAnimation(DEMONATTACKUP, framespeed));

		anim = state.getAnimation(DEMONATTACKDOWN);
		if(anim == null)
			state.storeAnimation(DEMONATTACKDOWN, state.createGameAnimation(DEMONATTACKDOWN, framespeed));
		
		//angel animations
		
		anim = state.getAnimation(ANGELWALKDOWN);
		if(anim == null)
			state.storeAnimation(ANGELWALKDOWN, state.createGameAnimation(ANGELWALKDOWN, framespeed));

		anim = state.getAnimation(ANGELWALKUP);
		if(anim == null)
			state.storeAnimation(ANGELWALKUP, state.createGameAnimation(ANGELWALKUP, framespeed));

		anim = state.getAnimation(ANGELWALKSIDE);
		if(anim == null)
			state.storeAnimation(ANGELWALKSIDE, state.createGameAnimation(ANGELWALKSIDE, framespeed));

		anim = state.getAnimation(ANGELATTACKSIDE);
		if(anim == null)
			state.storeAnimation(ANGELATTACKSIDE, state.createGameAnimation(ANGELATTACKSIDE, framespeed));

		anim = state.getAnimation(ANGELATTACKUP);
		if(anim == null)
			state.storeAnimation(ANGELATTACKUP, state.createGameAnimation(ANGELATTACKUP, framespeed));

		anim = state.getAnimation(ANGELATTACKDOWN);
		if(anim == null)
			state.storeAnimation(ANGELATTACKDOWN, state.createGameAnimation(ANGELATTACKDOWN, framespeed));
		
		//--player projectiles 
		anim = state.getAnimation(HALOPROJECTILEV);
		if(anim == null)
			state.storeAnimation(HALOPROJECTILEV, state.createGameAnimation(HALOPROJECTILEV, .0333f));
		
		
		anim = state.getAnimation(HALOPROJECTILEH);
		if(anim == null)
			state.storeAnimation(HALOPROJECTILEH, state.createGameAnimation(HALOPROJECTILEH, .0333f));
		
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		if (transforming)
		{
			transformTime -= delta;
			if (transformTime < 0)
			{
				setCurrentForm(targetForm);
				transformTime = 0.75f;
			}
			getMove().setDirection(0, 0);
			return;
		}
//		AnimationController animation = getAnimation();
//		Vector2 vel = getVelocity();
//		// TODO: for now it doesnt update animations if player is flying to tell it
//		// apart from its grounded form . later we add some sort of shadow and gradually
//		// increase the depth
//		if (!getMove().isFlying())
//			animation.setPlaySpeed(1f);
//		else
//			animation.setPlaySpeed(0f);

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
					if (itemID == Collectible.KEY) {
						m_numKeys++;
						System.out.println("Keys + 1");

					}

					System.out.println(StringUtils.format("%s collected itemId[%s]", getName(), itemID));

					// collect the item so that it cannot be collected again
					c.collect();

				}

			}
		}

		//Handle robe physics
		if (this.form == form.Angel)
		{
			MoveControl move = getMove();
			float cxd = move.getXDir();
			if (lastXDir != cxd)
			{
				if (cxd == 0)
				{
					legsOffsetXTarget = 0;
					legsDeltaX = -lastXDir * 0.1f;
					legsDeltaXFactor = 0.05f;
				} else
				{
					legsOffsetXTarget = -5 * cxd;
					legsDeltaX = -cxd * 0.2f;
					legsDeltaXFactor = 0.05f;
				}
				legsBounceX = false;
				lastXDir = cxd;
			}

			legsOffsetX += legsDeltaX;
			if (legsOffsetXTarget >= 0)
			{
				if (legsOffsetX > legsOffsetXTarget)
				{
					if (legsBounceX == false)
					{
						legsBounceX = true;
						legsOffsetXTarget -= 1;
						if (legsOffsetXTarget < 1)
						{
							legsOffsetXTarget = 1;
							legsOffsetX = legsOffsetXTarget;
							legsDeltaXFactor = 0;
							legsDeltaX = 0;
						}
					}
					legsDeltaX -= legsDeltaXFactor;
				} else
				{
					legsBounceX = false;
					legsDeltaX += legsDeltaXFactor;
				}
			} else if (legsOffsetXTarget < 0)
			{
				if (legsOffsetX < legsOffsetXTarget)
				{
					if (legsBounceX == false)
					{
						legsBounceX = true;
						legsOffsetXTarget += 1;
						if (legsOffsetXTarget > -1)
						{
							legsOffsetXTarget = -1;
							legsOffsetX = legsOffsetXTarget;
							legsDeltaXFactor = 0;
							legsDeltaX = 0;
						}
					}
					legsDeltaX += legsDeltaXFactor;
				} else
				{
					legsBounceX = false;
					legsDeltaX -= legsDeltaXFactor;
				}
			}
			if (legsDeltaX < - 0.3f) legsDeltaX = -0.3f;
			if (legsDeltaX > 0.3f) legsDeltaX = 0.3f;

			dlegs.setPosition(legsX + legsOffsetX, legsY + legsOffsetY);
			playerShadow.setPosition(20 + shadowOffset + legsOffsetX, legsOffsetY - 3);
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
					Projectile p = Projectile.get(HealthGroup.Angel,HealthGroup.Demon,HealthGroup.Neutral); // get a pooled projectile
					p.setVelocity(0, 0);
					float w = p.getWidth();
					float h = p.getHeight();
					switch (getDirection()) {

					case Right:
						p.setPosition(getX() + getWidth() * 0.3f, getY() + getHeight() * .75f + getDepth());
						p.getAnimation().setAnimation(HALOPROJECTILEH, PlayMode.NORMAL);
						p.setFlip(false, false);
						p.setSize(63, 9);
						p.setBounds(w*0.3f, h*0.15f, w*0.3f, h*0.6f);
						break;
					case Left:
						p.setPosition(getX() + getWidth() * 0.1f, getY() + getHeight() * .75f + getDepth());
						p.getAnimation().setAnimation(HALOPROJECTILEH, PlayMode.NORMAL);
						p.setFlip(true, false);
						p.setSize(63, 9);
						p.setBounds(w*0.5f, h*0.15f, w*0.3f, h*0.6f);
						break;
					case Up:
						p.setSize(52, 46);
						p.setPosition(getX() + getWidth() * .5f - (p.getWidth()*.5f), getY() + getHeight() + getDepth() - p.getHeight());
						p.getAnimation().setAnimation(HALOPROJECTILEV, PlayMode.NORMAL);
						p.setFlip(false, false);
						p.setBounds(w*0.2f, h*0.3f, w*0.6f, h*0.4f);
						break;
					case Down:
						p.setSize(52, 46);
						p.setPosition(getX() + getWidth() * .5f -(p.getWidth() *.5f), getY() + getDepth() + p.getHeight());
						p.getAnimation().setAnimation(HALOPROJECTILEV, PlayMode.NORMAL);
						p.setFlip(false, true);
						p.setBounds(w*0.2f, h*0.3f, w*0.6f, h*0.4f);
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

				MeleeAttack m = MeleeAttack.get(HealthGroup.Angel, HealthGroup.Demon, HealthGroup.Neutral);

				setupMelee(m);

				getGameLayer().addGameObject(m, KyperBoxGame.NULL_PROPERTIES);

			}
		};
	}

	public boolean isTransforming()
	{
		return transforming;
	}

	//animation listener
	public void finished(String name, int playTime)
	{
		if (name == TRANSFORM)
		{
			if (transforming == false) return;
			transformSprite.setVisible(false);
			setPreDrawChildren(true);
			transforming = false;
			transformTime = 0;
			getMove().setDirection(0,0);
			setVelocity(0,0);
			//System.out.println("Finished");
		}
	}
}
