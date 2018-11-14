package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.ZOrderControl;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.controllers.CollisionController;
import com.kyperbox.objects.GameObject;

public class Basic extends GameObject {
	
	
	private HealthControl health;
	private CollisionController collision;
	private AnimationController animation;
	private MoveControl move;
	private ZOrderControl zorder;
	
	
	public Basic() {
		collision = new CollisionController();
		animation = new AnimationController();
		health = new HealthControl(HealthGroup.Neutral,10f);
		move = new MoveControl(200);
		zorder = new ZOrderControl();
		zorder.setZOrder(ZOrder.PLAYER);
		setApplyVelocity(false);

	}
	
	public ZOrderControl getZOrder() {
		return zorder;
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
	
	public HealthControl getHealth() {
		return health;
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
		addController(health);
		addController(zorder);
	}

	@Override
	public void onRemove() {
		super.onRemove();
		removeController(collision);
		removeController(move);
		removeController(animation);
		removeController(health);
		removeController(zorder);
	}

}
