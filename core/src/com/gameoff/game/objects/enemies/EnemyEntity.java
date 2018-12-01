package com.gameoff.game.objects.enemies;

import com.gameoff.game.GameLevel;
import com.gameoff.game.Sounds;
import com.gameoff.game.objects.DirectionEntity;
import com.gameoff.game.objects.Door;
import com.kyperbox.GameState;
import com.kyperbox.objects.GameLayer;

/**
 * all enemies should extend this.
 *
 */
public class EnemyEntity extends DirectionEntity{
	
	public void hit() {
		getState().playSound(Sounds.DemonHit);
	}

  @Override
  public void onRemove() {
    super.onRemove();
    //System.out.println("Number Enemies: " + GameLevel.getCurrentLevel().getCurrentRoom().numberEnemies);
    if (GameLevel.getCurrentLevel().getCurrentRoom().numberEnemies > 0)
    {
      GameLevel.getCurrentLevel().getCurrentRoom().numberEnemies--;
      if (GameLevel.getCurrentLevel().getCurrentRoom().numberEnemies < 1)
      {
    	  
    	getState().playSound(Sounds.DoorsOpened);
        //all enemies killed.
        GameState state = getState();
        GameLayer layer = state.getPlaygroundLayer();
        for(int i = 0; i < 4; i++)
        {
          Door d = (Door) layer.getGameObject("Door" + i);
          if (d != null && d.m_keyHole==null)
          {
            d.open();
            //System.out.println("***** Open all doors");
            //TODO: show a message, sound effect?
          }
        }
      }
    }
  }
}
