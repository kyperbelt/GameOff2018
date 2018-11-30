package com.gameoff.game.objects.enemies;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.gameoff.game.control.DirectionControl;
import com.gameoff.game.objects.*;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.objects.composition.DropTable;
import com.gameoff.game.objects.composition.Lootable;
import com.gameoff.game.objects.DirectionEntity;
import com.gameoff.game.*;
import com.kyperbox.GameState;
import com.kyperbox.managers.StateManager;
import com.kyperbox.objects.GameLayer;

/**
 * all enemies should extend this.
 *
 */
public class EnemyEntity extends DirectionEntity{

  @Override
  public void onRemove() {
    super.onRemove();
    //System.out.println("Number Enemies: " + GameLevel.getCurrentLevel().getCurrentRoom().numberEnemies);
    if (GameLevel.getCurrentLevel().getCurrentRoom().numberEnemies > 0)
    {
      GameLevel.getCurrentLevel().getCurrentRoom().numberEnemies--;
      if (GameLevel.getCurrentLevel().getCurrentRoom().numberEnemies < 1)
      {
        //all enemies killed.
        GameState state = getState();
        GameLayer layer = state.getPlaygroundLayer();
        for(int i = 0; i < 4; i++)
        {
          Door d = (Door) layer.getGameObject("Door" + i);
          if (d != null)
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
