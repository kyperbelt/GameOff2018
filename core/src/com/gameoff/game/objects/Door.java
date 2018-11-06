package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;

public class Door extends Basic {
  int m_code = 0;

  public Door(int doorCode) {
    getMove().setPassable(false);
    m_code = doorCode;
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);

    removeController(getHealth());
    //addController(attack);
    if (m_code == 1)
    {
      setSprite("door_open");
      getMove().setPhysical(false);
    }
    else if (m_code == 2)
    {
      setSprite("door_closed");
      getMove().setPhysical(true);
    }

  }

  public void open()
  {
    getMove().setPhysical(false);
    setSprite("door_open");
    m_code = 1; //maybe, think need to make a bit more involved than this
  }

}
