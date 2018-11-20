package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.ZOrder;

public class SpikeWall extends Basic {
  
  public SpikeWall() {
    setName("spikewall");
    setZIndex(ZOrder.FLOOR_TEXT);
  }
  
  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    removeController(getHealth());
    getMove().setPassable(false);
    if (properties != null)
    {
      int dir = properties.get("direction", 0, Integer.class);
      if (dir == 0)
      {
        setSprite("spikewall_n");
        setZIndex(ZOrder.PLAYER);
        setBounds(10,24,getWidth()-20,getHeight()-36);

      } else if (dir == 1)
      {
        setSprite("spikewall_e");
        setZIndex(ZOrder.FLOOR_TEXT);
        setBounds(12,5,getWidth()-28,getHeight()-10);
      } else if (dir == 2)
      {
        setSprite("spikewall_s");
        setZIndex(ZOrder.PLAYER);
        setBounds(12,5,getWidth()-28,getHeight()-10);
      } else if (dir == 3)
      {
        setSprite("spikewall_w");
        setZIndex(ZOrder.FLOOR_TEXT);
        setBounds(12,5,getWidth()-28,getHeight()-10);
      }
    }
  }
}
