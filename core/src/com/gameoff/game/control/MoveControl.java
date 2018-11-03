package com.gameoff.game.control;

import com.badlogic.gdx.math.MathUtils;
import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

public class MoveControl extends GameObjectController {

	float moveSpeed = 0;
	float xDirection = 0;
	float yDirection = 0;

	boolean flying = false;
	boolean passable = false;
	boolean physical = true;

	public MoveControl(float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public void setFlying(boolean flying) {
		this.flying = flying;
	}

	/**
	 * if the object attached to this control is flying and therefore avoiding
	 * passable non-flying objects
	 * 
	 * @return
	 */
	public boolean isFlying() {
		return flying;
	}

	/**
	 * set if the object attached to this control is pass-able by flying objects
	 * when it , itself is not flying
	 * 
	 * @param passable
	 */
	public void setPassable(boolean passable) {
		this.passable = passable;
	}

	/**
	 * true if the object attached to this control is pass-able by flying objects
	 * when it , itself is not flying
	 * 
	 * @return
	 */
	public boolean isPassable() {
		return passable;
	}

	public void setPhysical(boolean physical) {
		this.physical = physical;
	}

	public boolean isPhysical() {
		return physical;
	}

	public void setMoveSpeed(float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public float getMoveSpeed() {
		return moveSpeed;
	}

	public void setXDir(float xDirection) {
		this.xDirection = MathUtils.clamp(xDirection, -1f, 1f);
	}

	public void setYDir(float yDirection) {
		this.yDirection = MathUtils.clamp(yDirection, -1f, 1f);
	}

	public void setDirection(float xDirection, float yDirection) {
		setXDir(xDirection);
		setYDir(yDirection);
	}

	public float getXDir() {
		return xDirection;
	}

	public float getYDir() {
		return yDirection;
	}

	@Override
	public void init(GameObject o) {
		setDirection(0, 0);
	}

	@Override
	public void remove(GameObject o) {

	}

	@Override
	public void update(GameObject o, float delta) {

	}

}
