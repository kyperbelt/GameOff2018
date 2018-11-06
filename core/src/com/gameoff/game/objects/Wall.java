package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;

public class Wall extends Basic {
	
	public Wall() {
		
	}
	
	@Override
	public void init(MapProperties properties) {
		super.init(properties);
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
