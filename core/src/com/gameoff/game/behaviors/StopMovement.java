package com.gameoff.game.behaviors;

import com.gameoff.game.Context;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.StateControl;
import com.gameoff.game.control.StateControl.EntityState;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.objects.GameObject;

/**
 * 
 * behavior to stop all movement in the game objects move control
 * @author john
 *
 */
public class StopMovement extends BehaviorNode{

	GameObject self;
	MoveControl move;
	StateControl state;
	
	@Override
	public void init() {
		super.init();
		
		self = getContext().get(Context.SELF,GameObject.class);
		move = self.getController(MoveControl.class);
		state = self.getController(StateControl.class);
	
	}
	
	
	@Override
	public NodeState update(float delta) {
		super.update(delta);
	
		if ((move!=null) && (state.getState() != EntityState.Jumping) && (state.getState() != EntityState.Falling)) {
			move.setDirection(0, 0);
			return NodeState.Success;
		}

		return NodeState.Failure;
	}
	
	
}
