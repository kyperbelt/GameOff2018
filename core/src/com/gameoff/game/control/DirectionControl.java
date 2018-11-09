package com.gameoff.game.control;

import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

public class DirectionControl extends GameObjectController{
	

	public enum Direction {
		Left, Right, Up, Down
	}
	
	Direction direction;
	DirectionChangeListener listener;
	
	public DirectionControl(Direction direction) {
		this.direction = direction;
	}
	
	public void setDirection(Direction direction) {
		if(this.direction!=direction)
			if(this.listener != null)
				listener.directionChanged(this.direction, direction);
		this.direction = direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public void setDirectionListener(DirectionChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public void init(GameObject object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameObject object, float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(GameObject object) {
		// TODO Auto-generated method stub
		
	}
	
	public interface DirectionChangeListener{
		public void directionChanged(Direction lastDirection,Direction newDirection);
	}

}
