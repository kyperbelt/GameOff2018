package com.gameoff.game.behaviors;

import com.gameoff.game.Context;
import com.gameoff.game.control.DirectionControl;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.DirectionControl.Direction;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.objects.GameObject;

public class InvertAxisMovement extends BehaviorNode {

	private static final int XAXIS = 0;
	private static final int YAXIS = 1;

	int axis = 0;
	GameObject self;
	MoveControl move;
	DirectionControl dir;

	public InvertAxisMovement(String axis) {
		if (axis.equalsIgnoreCase("y") || axis.equalsIgnoreCase("vertical")) {
			this.axis = YAXIS;
		} else if (axis.equalsIgnoreCase("x") || axis.equalsIgnoreCase("horizontal")) {
			this.axis = XAXIS;
		}
	}

	@Override
	public void init() {
		super.init();
		self = getContext().get(Context.SELF, GameObject.class);
		move = self.getController(MoveControl.class);
		dir = self.getController(DirectionControl.class);

	}

	@Override
	public NodeState update(float delta) {

		if (move != null) {

			if (axis == XAXIS) {
				move.setXDir(-move.getXDir());
				if (dir != null) {
					if (move.getXDir() > 0) {
						dir.setDirection(Direction.Right);
					}else if(move.getXDir() < 0) {
						dir.setDirection(Direction.Left);
					}
				}

			} else {
				move.setYDir(-move.getYDir());
			}

			return NodeState.Success;
		}
		return NodeState.Failure;
	}

}
