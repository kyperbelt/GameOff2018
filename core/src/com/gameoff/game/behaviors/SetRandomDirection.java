package com.gameoff.game.behaviors;

import com.badlogic.gdx.math.Vector2;
import com.gameoff.game.Context;
import com.gameoff.game.control.DirectionControl;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.MoveControl;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.controllers.CollisionController;
import com.kyperbox.objects.GameObject;
import java.util.Random;

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
  Random m_random = new Random();

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
    super.update(delta);

    NodeState state = NodeState.Success;

    int v = m_random.nextInt(8);

    if (v == 0)
    {
      //up
      direction.setDirection(Direction.Up);
      move.setDirection(0, -1);
    } else if (v == 1)
    {
      //up and right
      direction.setDirection(Direction.Right);
      move.setDirection(1, -1);
    } else if (v == 2)
    {
      //right
      direction.setDirection(Direction.Right);
      move.setDirection(1, 0);
    } else if (v == 3)
    {
      //right and down
      direction.setDirection(Direction.Right);
      move.setDirection(1, 1);
    } else if (v == 4)
    {
      //down
      direction.setDirection(Direction.Down);
      move.setDirection(0, 1);
    } else if (v == 5)
    {
      //down and left
      direction.setDirection(Direction.Left);
      move.setDirection(-1, 1);
    } else if (v == 6)
    {
      //left
      direction.setDirection(Direction.Left);
      move.setDirection(-1, 0);
    } else if (v == 7)
    {
      //up and left
      direction.setDirection(Direction.Left);
      move.setDirection(-1, -1);
    }

      //Vector2 position = self.getCollisionCenter();
      //Vector2 destination = target.getCollisionCenter();

    return setState(state);
  }

}
