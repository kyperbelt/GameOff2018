package com.gameoff.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.gameoff.game.control.PlayerControl;
import com.kyperbox.controllers.AnimationController;

public class Player extends Basic {

	public enum PlayerState {
		Moving, Attacking
	}

	PlayerControl control;
	public String state; // will probably change this to its own state to make it easy to have states
							// such as "walking","attacking","damaged","dying", ect.

	public Player() {
		// we set this control to the id of 0 which is corresponding of the
		// PlayerControls in PlayerControlSystem
		// We can support more than one player but must add the playerControls to the
		// PlayerControlSystem
		control = new PlayerControl(0);
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		// we must add a controller in the init method since all controlers get removed
		// from objects when the objects are removed from the gamestate/gamelayer
		addController(control);

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
		animation.addAnimation("down", "player_walk_down");
		animation.addAnimation("up", "player_walk_up");
		animation.addAnimation("left", "player_walk_left");
		animation.addAnimation("right", "player_walk_right");
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
		if (vel.x != 0 || vel.y != 0) {
			if (Math.abs(vel.x) >= Math.abs(vel.y)) {
				if (vel.x > 0) {
					setAnimation("right");
				} else {
					setAnimation("left");
				}
			} else {
				if (vel.y > 0) {
					setAnimation("up");
				} else {
					setAnimation("down");
				}
			}
		} else {
			animation.setPlaySpeed(0);
		}
	}

	public void setAnimation(String animation) {
		if (state == null || !state.equals(animation)) {
			getAnimation().set(animation, PlayMode.LOOP);
			state = animation;
		}
	}
}
