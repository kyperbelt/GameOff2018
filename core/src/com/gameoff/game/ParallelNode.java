package com.gameoff.game;

import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.CompositeNode;
import com.kyperbox.ai.NodeState;

public class ParallelNode extends CompositeNode{

	boolean finished = false;
	
	@Override
	public void init() {
		super.init();
		finished = false;
	}
	
	@Override
	public NodeState update(float delta) {
		super.update(delta);
		
		finished = true;
		for (int i = 0; i < getNodes().size; i++) {
			BehaviorNode n = getNodes().get(i);
			if(n.update(delta) == NodeState.Running)
				finished = false;
		}
		
		
		if(finished)
			return NodeState.Success;
		
		return NodeState.Running;
	}

}
