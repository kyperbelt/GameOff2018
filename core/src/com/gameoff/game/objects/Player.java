package com.gameoff.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.gameoff.game.control.AttackControl;
import com.gameoff.game.control.AttackControl.AttackListener;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.PlayerControl;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.umisc.StringUtils;

public class Player extends Basic {

	public enum PlayerState {
		Idling, Moving, Dashing, Attacking, Damaged
	}

	public enum Form {
		Demon, Angel
	}

	public enum Direction {
		Left, Right, Up, Down
	}

	float angelSpeed = 200;
	float demonSpeed = 120;

	PlayerControl control;
	AttackControl attack;
	//attack listeners
	float basicProjectileCD = .35f;
	AttackListener basicProjectile;
	float basicMeleeCD = .75f;
	AttackListener basicMelee;
	
	
	
	String animation;
	Direction direction;
	Form form;
	PlayerState state;// will probably change this to its own state to make it easy to have states
						// such as "walking","attacking","damaged","dying", ect.

	public Player() {
		// we set this control to the id of 0 which is corresponding of the
		// PlayerControls in PlayerControlSystem
		// We can support more than one player but must add the playerControls to the
		// PlayerControlSystem
		control = new PlayerControl(0);
		attack = new AttackControl(null);
		
		setupBasicProjectile();
		setUpBasicMelee();

	}

	public void setDirection(Direction dir) {
		this.direction = dir;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setCurrentForm(Form form) {
		this.form = form;
		switch (form) {
		case Demon:
			getMove().setMoveSpeed(demonSpeed);
			attack.setAttackListener(basicMelee);
			attack.setCooldown(basicMeleeCD);
			break;
		case Angel:
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
		this.state = state;
	}

	public PlayerState getPlayerState() {
		return state;
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		// we must add a controller in the init method since all controlers get removed
		// from objects when the objects are removed from the gamestate/gamelayer
		addController(control);
		addController(attack);

		// we can set the collision bounds of this object independent of its actual
		// size.
		// collision bounds is what is actually used by the collisionSystem which is a
		// QuadTree in our case
		setCollisionBounds(getWidth() * .2f, 0, getWidth() * .6f, getHeight() * .8f);

		// this isnt the best way to do it since init gets called each time the object
		// is loaded in but later we can move this
		// to a static method that loads it once per state load. Animations can be
		// reused between objects to save on space
		float framespeed = .15f;
		getState().storeAnimation("player_walk_up", getState().createGameAnimation("player_walk_up", framespeed));
		getState().storeAnimation("player_walk_down", getState().createGameAnimation("player_walk_down", framespeed));
		getState().storeAnimation("player_walk_left", getState().createGameAnimation("player_walk_left", framespeed));
		getState().storeAnimation("player_walk_right", getState().createGameAnimation("player_walk_right", framespeed));

		AnimationController animation = getAnimation();
		animation.addAnimation("walk_down", "player_walk_down");
		animation.addAnimation("walk_up", "player_walk_up");
		animation.addAnimation("walk_left", "player_walk_left");
		animation.addAnimation("walk_right", "player_walk_right");

		setCurrentForm(Form.Demon);
		setPlayerState(PlayerState.Idling);
		setDirection(Direction.Up);
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
		// TODO: Refactor
		if (state == PlayerState.Moving && (vel.x != 0 || vel.y != 0)) {
			if (Math.abs(vel.x) >= Math.abs(vel.y)) {
				if (vel.x > 0) {
					setAnimation("walk_right");
					setDirection(Direction.Right);
				} else {
					setAnimation("walk_left");
					setDirection(Direction.Left);
				}
			} else {
				if (vel.y > 0) {
					setAnimation("walk_up");
					setDirection(Direction.Up);
				} else {
					setAnimation("walk_down");
					setDirection(Direction.Down);
				}
			}
		} else if (state == PlayerState.Idling) {
			animation.setPlaySpeed(0);
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
		switch (direction) {
		case Up:
		case Down:
			melee.setSize(getHeight() * 2f, getHeight()*.75f);
			melee.setBounds(0, 0, getHeight() * 2f, getHeight()*.75f);
			break;
		case Left:
		case Right:
			melee.setSize(getHeight()*.75f, getHeight() * 2f);
			melee.setBounds(0, 0, getHeight()*.75f, getHeight() * 2f);
			break;
		}
	}
	
	private void setMeleePos(MeleeAttack melee) {
		Vector2 center = getCollisionCenter();
		switch (direction) {
		case Up:
			melee.setPosition(center.x - melee.getWidth() * .5f, center.y + getBoundsRaw().height * .5f);
			break;
		case Down:
			melee.setPosition(center.x - melee.getWidth() * .5f, getY()-melee.getHeight());
			break;
		case Left:
			melee.setPosition(getX()-melee.getWidth(), center.y - melee.getHeight() * .5f);
			break;
		case Right:
			melee.setPosition(center.x + getBoundsRaw().width*.5f, center.y - melee.getHeight() * .5f);
			break;
		}
	}
	
	//attack listeners
	private void setupBasicProjectile() {

		basicProjectile = new AttackListener() {
			@Override
			public void onAttack() {

				if (form != null) {
					Projectile p = Projectile.get(); // get a pooled projectile
					p.setVelocity(0, 0);
					switch (direction) {
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
					switch (direction) {
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
	
	private void setUpBasicMelee() {
		
		basicMelee = new AttackListener() {
			
			@Override
			public void onAttack() {
				
				
				MeleeAttack m = MeleeAttack.get();
				
				setupMelee(m);
				
				getGameLayer().addGameObject(m, KyperBoxGame.NULL_PROPERTIES);
				
			}
		};
	}
}
