package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.GameOffGame;
import com.gameoff.game.control.*;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.*;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.kyperbox.GameState;
import com.kyperbox.controllers.AnimationController;
import com.gameoff.game.ZOrder;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class DoorMat extends Basic {

  Door m_door = null;
  public DoorMat(Door d) {
    getMove().setPassable(true);
    m_door = d;
    //setSprite("noregion");
    setSize(m_door.getWidth() -60, 5);
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    removeController(getHealth());
  }
  
  @Override
  public void update(float delta) {
    
    super.update(delta);
    
    Array<CollisionData> cols = getCollision().getCollisions();
    if(cols.size > 0) {
      for (int i = 0; i < cols.size; i++) {
      CollisionData d = cols.get(i);
      GameObject target = d.getTarget();
      if(target instanceof Player) {   
        System.out.println("HIT DOOR MAT!");
          Player p = (Player)target;
          if (p.useKey())
          {
            m_door.unlock();
            m_door.open();
          }
        }
      }
    }
  }
}
