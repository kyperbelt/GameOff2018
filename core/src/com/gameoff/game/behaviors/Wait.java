package com.gameoff.game.behaviors;

import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.umisc.StringUtils;

public class Wait extends BehaviorNode{

	float time = 1;
	float elapsed = 0;
	
	public Wait(float time) {
		this.time = Math.max(this.time, time);
	}
	
	@Override
	public void init() {
		super.init();
		elapsed = 0;
	}
	
	@Override
	public NodeState update(float delta) {
		super.update(delta);
		
		elapsed+=delta;
		
		if(elapsed >= time) {
			return setState(NodeState.Success);
		}
		
		System.out.println(StringUtils.format("waiting elapsed=%s time=%s", elapsed,time));
		return setState(NodeState.Running);
	}

}
