package com.gameoff.game.behaviors;

import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;

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
	}

	@Override
	public NodeState update(float delta) {
		return super.update(delta);
	}

}
