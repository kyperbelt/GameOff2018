package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.GameOffGame;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.kyperbox.GameState;
import com.kyperbox.controllers.AnimationController;

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


    AnimationController animation = getAnimation();
    animation.addAnimation("door_open", "door_open");

    //TEMP alwasy locked
    if (m_code == 1)
    {
      setSprite("door_open_3");
      getMove().setPhysical(false);
      open();
    }
    else if (m_code == 2)
    {
      setSprite("door_open_0");
      getMove().setPhysical(true);
      close();
    }
    getAnimation().setPlaySpeed(2f);

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

  public static void createDoorAnimations(GameState state) {
    float framespeed = .2f;
    state.storeAnimation("door_open", state.createGameAnimation("door_open", framespeed));
    state.storeAnimation("door_close", state.createGameAnimation("door_close", framespeed));
  }

  public void open()
  {
    getMove().setPhysical(false);
    getAnimation().set("door_open", PlayMode.NORMAL);
    getAnimation().setPlaySpeed(1f);
    m_code = 1;
  }

  public void close()
  {
    getMove().setPhysical(true);
    getAnimation().set("door_open", PlayMode.REVERSED);
    getAnimation().setPlaySpeed(1f);
    m_code = 2;
  }

}
