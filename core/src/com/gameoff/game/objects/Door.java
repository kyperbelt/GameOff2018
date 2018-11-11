package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.GameOffGame;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;

public class Door extends Basic {
  int m_code = 0;

  public Door(int doorCode) {
    getMove().setPassable(false);
    m_code = doorCode;
    getZOrder().setZOrder(3);//set the zorder after the player so it renders under it
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
  
  @Override
	public void update(float delta) {
	  
	  super.update(delta);
	  
	  Array<CollisionData> cols = getCollision().getCollisions();
	  if(m_code == 1 && cols.size > 0) {
		  for (int i = 0; i < cols.size; i++) {
			CollisionData d = cols.get(i);
			GameObject target = d.getTarget();
			if(target instanceof Player) {
				GameOffGame.log(this.getClass().getSimpleName(), "Example of player colliding with door.");
			}
		}
	  }
	}

  public void open()
  {
    getMove().setPhysical(false);
    setSprite("door_open");
    m_code = 1; //maybe, think need to make a bit more involved than this
  }

}
