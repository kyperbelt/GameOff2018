package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.gameoff.game.control.DirectionControl;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.objects.composition.DropTable;
import com.gameoff.game.objects.composition.Lootable;

/**
 * an entity that can move and face different directions
 * 
 * @author jonathancamarena
 *
 */
public class DirectionEntity extends Basic implements Lootable{

	DirectionControl direction;
	DropTable<Integer> drop;
	private int minDrop = 1;
	private int maxDrop = 2;
	
	public DirectionEntity() {
		direction = new DirectionControl(Direction.Down);
		drop = new DropTable<Integer>();
		drop.addDrop(Collectible.NONE, 1f);
	}
	
	/**
	 * max amount of items to drop
	 * @param itemDrop
	 */
	public void maxItemDrop(int itemDrop) {
		this.maxDrop = Math.max(2, itemDrop);
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
	
	public DropTable<Integer> getDropTable() {
		return drop;
	}

	@Override
	public int loot() {
		return getDropTable().getDrop();
	}

	@Override
	public int lootAmount() {
		return MathUtils.random(minDrop, maxDrop);
	}

}
