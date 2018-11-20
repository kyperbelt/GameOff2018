package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.ZOrder;

public class Wall extends Basic {
	
	public Wall() {
		setName("wall");
		setZIndex(ZOrder.BACKGROUND);
	}
	
	@Override
	public void init(MapProperties properties) {
		super.init(properties);
    removeController(getHealth());
    if (properties == null)
    {
      getMove().setPassable(false);
    } else
    {
      //if no passable bool property found then it is set to false
      getMove().setPassable(properties.get("passable",false,Boolean.class));
    }
	}
}
