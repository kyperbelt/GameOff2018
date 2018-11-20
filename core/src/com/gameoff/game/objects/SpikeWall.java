package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.ZOrderControl;
import com.kyperbox.objects.BasicGameObject;

public class SpikeWall extends Basic {
  
  public SpikeWall() {
    setName("spikewall");
  }
  
  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    removeController(getHealth());
    getMove().setPassable(false);
    ZOrderControl z = getZOrder();
    if (properties != null)
    {
      int dir = properties.get("direction", 0, Integer.class);
      if (dir == 0)
      {
        setSprite("spikewall_n");
        z.setZOrder(ZOrder.PLAYER);
        setBounds(10,24,getWidth()-20,getHeight()-36);

      } else if (dir == 1)
      {
        setSprite("spikewall_e");
        z.setZOrder(ZOrder.FLOOR_TEXT);
        setBounds(12,5,getWidth()-28,getHeight()-15);
        BasicGameObject overlay = new BasicGameObject();
        overlay.setSprite("spikewall_e_overlay");
        overlay.setSize(getWidth(), getHeight());
        ZOrderControl zo = new ZOrderControl();
        zo.setZOrder(ZOrder.FOREGROUND);
        overlay.addController(zo);
        this.getGameLayer().addGameObject(overlay, null);
        overlay.setPosition(getX(), getY());
      } else if (dir == 2)
      {
        setSprite("spikewall_s");
        z.setZOrder(ZOrder.PLAYER);
        setYOffset(-80);
        setBounds(12,5,getWidth()-28,getHeight()+5);
      } else if (dir == 3)
      {
        setSprite("spikewall_w");
        z.setZOrder(ZOrder.FLOOR_TEXT);
        setBounds(12,5,getWidth()-28,getHeight()-15);
        BasicGameObject overlay = new BasicGameObject();
        overlay.setSprite("spikewall_w_overlay");
        overlay.setSize(getWidth(), getHeight());
        ZOrderControl zo = new ZOrderControl();
        zo.setZOrder(ZOrder.FOREGROUND);
        overlay.addController(zo);
        this.getGameLayer().addGameObject(overlay, null);
        overlay.setPosition(getX(), getY());
      }
    }
  }
}
