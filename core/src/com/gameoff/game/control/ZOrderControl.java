package com.gameoff.game.control;

import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

/**
 * used to to control the order objects are drawn in the world. If two objects
 * have the same zorder number then ysorting will be used. Default zorder is 2;
 * 
 * @author john
 *
 */
public class ZOrderControl extends GameObjectController {

	int zOrder = 2;

	/**
	 * set the zOrder to render the object in. Lower number means it will be drawn
	 * on top of objects with higher zorder number.
	 * 
	 * @param zOrder
	 */
	public void setZOrder(int zOrder) {
		this.zOrder = zOrder;
	}

	public int getZOrder() {
		return zOrder;
	}

	@Override
	public void init(GameObject object) {

	}

	@Override
	public void update(GameObject object, float delta) {

	}

	@Override
	public void remove(GameObject object) {

	}

}
