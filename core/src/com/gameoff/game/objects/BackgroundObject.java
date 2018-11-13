package com.gameoff.game.objects;

import com.kyperbox.objects.GameObject;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.*;

//always sorted to back in YSortSystem
public class BackgroundObject extends GameObject{
  
  private ZOrderControl zorder;

  public BackgroundObject()
  {
    zorder = new ZOrderControl();
    zorder.setZOrder(ZOrder.BACKGROUND);
  }
}
