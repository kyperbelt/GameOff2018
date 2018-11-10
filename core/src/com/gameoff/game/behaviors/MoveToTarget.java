package com.gameoff.game.behaviors;

import com.badlogic.gdx.math.Vector2;
import com.gameoff.game.Context;
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
public class MoveToTarget extends BehaviorNode {

	GameObject self;
	MoveControl move;
	CollisionController cc;

	String targetName;
	GameObject target;

	public MoveToTarget(String targetName) {
		this.targetName = targetName;
	}

	@Override
	public void init() {
		super.init();
		self = getContext().get(Context.SELF, GameObject.class);
		move = self.getController(MoveControl.class);

		target = getContext().get(targetName, GameObject.class);
	}

	@Override
	public NodeState update(float delta) {
		super.update(delta);

		NodeState state = NodeState.Failure;
		if (self != null && move != null) {
			Vector2 position = self.getCollisionCenter();


			float dx = 0;
			float dy = 0;
			if (target != null) {
				Vector2 destination = target.getCollisionCenter();
				dx = destination.x - position.x;
				dy = destination.y - position.y;
				float length = (float)(Math.sqrt(dx*dx + dy*dy));
				
				dx /= length;
				dy /= length;
				
				state = NodeState.Success;
			}
			
			move.setDirection(dx, dy);
		}

		return setState(state);
	}

}
