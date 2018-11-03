package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.control.MoveControl;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.controllers.CollisionController;
import com.kyperbox.objects.GameObject;

public class Basic extends GameObject {
	
	
	private CollisionController collision;
	private AnimationController animation;
	private MoveControl move;
	
	
	public Basic() {
		collision = new CollisionController();
		animation = new AnimationController();
		move = new MoveControl(200);
		setApplyVelocity(false);

	}
	
	/**
	 * get the collision controller for this object. 
	 * faster than using o.getController(Controller.class)
	 * @return
	 */
	public CollisionController getCollision() {
		return collision;
	}
	
	/**
	 * get the animation controller for this object. 
	 * Same as above
	 * @return
	 */
	public AnimationController getAnimation() {
		return animation;
	}
	
	/**
	 * get the move controller
	 * @return
	 */
	public MoveControl getMove() {
		return move;
	}

	/**
	 * this method initiates all game objects. we are able to get properties from
	 * tmx and use them in this init method. This method gets called every time a
	 * gameobject is added to a layer
	 */
	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		addController(collision);
		addController(animation);
		addController(move);
	}

	@Override
	public void onRemove() {
		super.onRemove();
		removeController(collision);
		removeController(move);
		removeController(animation);
	}

}
