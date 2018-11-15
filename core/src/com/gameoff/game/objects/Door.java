package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.GameOffGame;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.kyperbox.GameState;
import com.kyperbox.controllers.AnimationController;
import com.gameoff.game.ZOrder;

public class Door extends Basic {
  int m_code = 0;
  String m_aName = "do";

  // 0 = no door, 1 = open, 2 = closed, 3 = closed and locked
  public Door(int doorCode, GameState state) {
    getMove().setPassable(false);
    m_code = doorCode;
    getZOrder().setZOrder(ZOrder.DOORS);
    m_aName = m_aName + doorCode;
    state.storeAnimation(m_aName, state.createGameAnimation("door_open", 0.2f));
  }

  private void doDoorState(boolean open)
  {
    if (open)
    {
      getMove().setPhysical(false);
      getAnimation().set("door_open", PlayMode.NORMAL);
    } else
    {
      getMove().setPhysical(true);
      getAnimation().set("door_open", PlayMode.REVERSED);
      getAnimation().setPlaySpeed(1f);
    }
    getAnimation().setPlaySpeed(1f);
  }

  @Override
  public void init(MapProperties properties) {
    super.init(properties);
    removeController(getHealth());

    AnimationController animation = getAnimation();
    animation.addAnimation("door_open", m_aName);

    if (m_code == 1) 
    {
      setSprite("door_open_3");
      getMove().setPhysical(false);
      doDoorState(true);
    }
    else
    {
      setSprite("door_open_0");
      getMove().setPhysical(true);
      doDoorState(false);
    }
    getAnimation().setPlaySpeed(2f);

  }
  
  @Override
	public void update(float delta) {
	  
	  super.update(delta);
	  
	  Array<CollisionData> cols = getCollision().getCollisions();
	  if(m_code == 3 && cols.size > 0) {
		  for (int i = 0; i < cols.size; i++) {
			CollisionData d = cols.get(i);
			GameObject target = d.getTarget();
			if(target instanceof Player) {
				if (m_code == 3)
         {        
            Player p = (Player)target;
            if (p.useKey())
            {
              unlock();
              open();
            }
         }
        }
		  }
	  }
	}

  public void unlock()
  {
    if (m_code == 3) 
    {
      m_code = 2;
    }
  }

  public void open()
  {
    if (m_code < 2) return;
    doDoorState(true);
    m_code = 1;
  }

  public void close()
  {
    if (m_code > 1) return;
    doDoorState(false);
    m_code = 2;
  }

  public int getCode()
  {
    return m_code;
  }

}
