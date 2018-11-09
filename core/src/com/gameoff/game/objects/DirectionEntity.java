package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.control.DirectionControl;
import com.gameoff.game.control.DirectionControl.Direction;

/**
 * an entity that can move and face different directions
 * 
 * @author jonathancamarena
 *
 */
public class DirectionEntity extends Basic {

	DirectionControl direction;
	
	public DirectionEntity() {
		direction = new DirectionControl(Direction.Down);
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		addController(direction);

	}

	@Override
	public void onRemove() {
		super.onRemove();
		removeController(direction);
	}

	public void setDirection(Direction dir) {
		this.direction.setDirection(dir);
	}

	public Direction getDirection() {
		return direction.getDirection();
	}

	public DirectionControl getDirectionControl() {
		return direction;
	}

}
