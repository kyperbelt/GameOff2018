package com.gameoff.game.behaviors;

import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.objects.GameObject;

/**
 * remove the object in the context 
 * @author john
 *
 */
public class RemoveObject extends BehaviorNode{
	
	private String contextName;
	
	private GameObject object;
	
	public RemoveObject(String contextName) {
		this.contextName = contextName;
	}
	
	@Override
	public void init() {
		super.init();
		object = getContext().get(contextName,GameObject.class);
	
	}
	
	@Override
	public NodeState update(float delta) {
		super.update(delta);
		
		if(object!=null) {
			object.remove();
			return setState(NodeState.Success);
		}
		
		return setState(NodeState.Failure);
	}

}
