package com.gameoff.game.behaviors;

import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.gameoff.game.Context;
import com.kyperbox.objects.GameObject;
import com.gameoff.game.objects.Door;
import com.kyperbox.GameState;
import com.kyperbox.objects.GameLayer;

/**
 * remove the object in the context 
 * @author john
 *
 */
public class UpdateDoor extends BehaviorNode {
  
  private String doorName;
  
  private Door[] doors = null;
  private boolean closeDoors;
  
  public UpdateDoor(String doorName, boolean closeDoors) {
    this.doorName= doorName;
    this.closeDoors = closeDoors;
  }
  
  @Override
  public void init() {
    super.init();
    GameObject self = getContext().get(Context.SELF, GameObject.class);
    if(self!=null) {
      GameState state = self.getState();
      GameLayer layer = state.getPlaygroundLayer();
      if (doorName.toLowerCase().equals("all"))
      {
        doors = new Door[4];
        for(int i = 0; i < 4; i++)
        {
          doors[i] = (Door) layer.getGameObject("Door" + i);
        }
      }
    }
  }
  
  @Override
  public NodeState update(float delta) {
    super.update(delta);
    if(doors != null) {
      for (int i =0; i < doors.length; i++)
      {
        if (doors[i] != null)
        {
          if (closeDoors == false)
            doors[i].open();
          else
            doors[i].close();
        }
      }

      return setState(NodeState.Success);
    }
    
    return setState(NodeState.Failure);
  }

}
