package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.ZOrderControl;
import com.kyperbox.objects.GameObject;
//always sorted to back in YSortSystem
public class ForegroundObject extends GameObject{
  
	ZOrderControl zorder;
	
	public ForegroundObject() {
		zorder = new ZOrderControl();
		zorder.setZOrder(ZOrder.FOREGROUND);
	}
	
	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		addController(zorder);
	}
	
	@Override
	public void onRemove() {
		super.onRemove();

		removeController(zorder);
	}

}
