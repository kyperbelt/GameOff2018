package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;

public class Pit extends Basic {
  
  public Pit() {
    getMove().setPassable(true);
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    removeController(getHealth());
  }
}
