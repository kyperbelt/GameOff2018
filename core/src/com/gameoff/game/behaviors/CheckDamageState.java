package com.gameoff.game.behaviors;

import com.gameoff.game.Context;
import com.gameoff.game.control.StateControl;
import com.gameoff.game.control.StateControl.EntityState;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.objects.GameObject;

public class CheckDamageState extends BehaviorNode{

	
	GameObject self;
	StateControl state;
	
	@Override
	public void init() {
		super.init();
		
		self = getContext().get(Context.SELF, GameObject.class);
		state = self.getController(StateControl.class);

		
	}
	
	@Override
	public NodeState update(float delta) {
		super.update(delta);
		
		if(state!=null) {
			if(state.getState() == EntityState.Damaged)
				return setState(NodeState.Success);
		}
	
		return setState(NodeState.Failure);
	}
	
}
