package com.gameoff.game.behaviors;

import com.gameoff.game.Context;
import com.gameoff.game.control.MoveControl;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.objects.GameObject;

/**
 * a behavior to check if the has valid movement in the given axis
 * (`x`/'horizontal' or `y`/'vertical')
 * 
 * @author jonathancamarena
 *
 */
public class CheckAxisMovement extends BehaviorNode {

	private static final int XAXIS = 0;
	private static final int YAXIS = 1;

	int axis = XAXIS;
	float check = 0.01f;

	float time;
	float elapsed;

	GameObject self;
	MoveControl move;

	float initialValue;

	public CheckAxisMovement(String axis, float check) {

		if (axis.equalsIgnoreCase("y") || axis.equalsIgnoreCase("vertical")) {
			this.axis = YAXIS;
		} else if (axis.equalsIgnoreCase("x") || axis.equalsIgnoreCase("horizontal")) {
			this.axis = XAXIS;
		}

		this.check = check;
	}

	@Override
	public void init() {
		super.init();
		self = getContext().get(Context.SELF, GameObject.class);
		move = self.getController(MoveControl.class);
		time = .1f;
		elapsed = 0;
	}

	@Override
	public NodeState update(float delta) {
		if(self!=null) {
			if(elapsed == 0f) {
				initialValue = axis == XAXIS ? self.getX() : self.getY();
			}
			
			elapsed+=delta;
			
			if(elapsed>=time) {
				
				float cv = axis == XAXIS ? self.getX() : self.getY();
				
				if(Math.abs(initialValue-cv)>check) {
					return NodeState.Success;
				}else {
					return NodeState.Failure;
				}

			}
			
			return NodeState.Running;
		}

		// no axis movement detected -- could be because no movement control found
		return NodeState.Failure;
	}

}
