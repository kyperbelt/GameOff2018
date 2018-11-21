package com.gameoff.game.behaviors;

import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;

public class TestNode extends BehaviorNode{
	
	private String testText;
	
	public TestNode(String testText) {
		
		this.testText = testText;
	}
	
	@Override
	public void init() {
		super.init();

	}
	
	@Override
	public NodeState update(float delta) {
		//super.update(delta);
		System.out.println(testText);
		return NodeState.Success;
		
	}

}
