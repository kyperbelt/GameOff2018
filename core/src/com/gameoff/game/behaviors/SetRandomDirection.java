package com.gameoff.game.behaviors;

import com.badlogic.gdx.math.MathUtils;
import com.gameoff.game.Context;
import com.gameoff.game.control.DirectionControl;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.MoveControl;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.controllers.CollisionController;
import com.kyperbox.objects.GameObject;

/**
 * moves to the target with the given name in the context. It will fail if no
 * target is present in the context
 * 
 * @author john
 *
 */
public class SetRandomDirection extends BehaviorNode {

  GameObject self;
  MoveControl move;
  CollisionController cc;
  DirectionControl direction;

  //String targetName;
  //GameObject target;

  public SetRandomDirection() {
    //this.targetName = targetName;
  }

  @Override
  public void init() {
    super.init();
    self = getContext().get(Context.SELF, GameObject.class);
    move = self.getController(MoveControl.class);
    direction = self.getController(DirectionControl.class);
    //target = getContext().get(targetName, GameObject.class);
  }

  @Override
  public NodeState update(float delta) {

    NodeState state = NodeState.Success;

    int v = MathUtils.random(4);

    if (v == 0)
    {
      //up right
      direction.setDirection(Direction.Right);
      move.setDirection(1, 1);
    } else if (v == 1)
    {
      //up left
      direction.setDirection(Direction.Left);
      move.setDirection(-1, 1);
    } else if (v == 2)
    {
      //down right
      direction.setDirection(Direction.Right);
      move.setDirection(1, -1);
    } else if (v == 3)
    {
      //rdown left
      direction.setDirection(Direction.Left);
      move.setDirection(-1, -1);
    } 
//    else if (v == 4)
//    {
//      //down
//      direction.setDirection(Direction.Down);
//      move.setDirection(0, 1);
//    } else if (v == 5)
//    {
//      //down and left
//      direction.setDirection(Direction.Left);
//      move.setDirection(-1, 1);
//    } else if (v == 6)
//    {
//      //left
//      direction.setDirection(Direction.Left);
//      move.setDirection(-1, 0);
//    } else if (v == 7)
//    {
//      //up and left
//      direction.setDirection(Direction.Left);
//      move.setDirection(-1, -1);
//    }

      //Vector2 position = self.getCollisionCenter();
      //Vector2 destination = target.getCollisionCenter();

    return state;
  }

}
