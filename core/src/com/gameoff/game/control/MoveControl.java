package com.gameoff.game.control;

import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

public class MoveControl extends GameObjectController{

	float moveSpeed = 0;
	float xDirection = 0;
	float yDirection = 0;
	
	boolean physical = true;
	
	
	public MoveControl(float moveSpeed) {
		this.moveSpeed = moveSpeed;
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
		this.xDirection = xDirection;
	}
	
	public void setYDir(float yDirection) {
		this.yDirection = yDirection;
	}
	
	public void setDirection(float xDirection,float yDirection) {
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
