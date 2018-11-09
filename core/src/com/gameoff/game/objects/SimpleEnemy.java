package com.gameoff.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.Context;
import com.gameoff.game.behaviors.TestNode;
import com.gameoff.game.control.AiControl;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.DirectionControl.DirectionChangeListener;
import com.gameoff.game.control.StateControl;
import com.gameoff.game.control.StateControl.EntityState;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.BehaviorTree;
import com.kyperbox.ai.RepeatNode;
import com.kyperbox.umisc.KyperSprite;
import com.kyperbox.umisc.UserData;

public class SimpleEnemy extends DirectionEntity {

	StateControl state;
	String animation;
	UserData context;
	AiControl ai;

	public SimpleEnemy() {
		state = new StateControl(EntityState.Moving);
		context = new UserData(getClass().getSimpleName() + "_Context");
		context.put(Context.SELF, this);
		ai = new AiControl(context, getExampleAi());
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		addController(state);
		addController(ai);
		createAnimations();

		if (getWidth() == 0) {
			setSize(32, 32);
			setCollisionBounds(0, 0, getWidth(), getHeight());
		}

		getDirectionControl().setDirectionListener(new DirectionChangeListener() {
			@Override
			public void directionChanged(Direction lastDirection, Direction newDirection) {
				if (state.getState() == EntityState.Moving) {
					switch (newDirection) {
					case Up:
						getAnimation().set("move_up");
						break;
					case Down:
						getAnimation().set("move_down");
						break;
					case Left:
						getAnimation().set("move_left");
						break;
					case Right:
						getAnimation().set("move_right");
						break;

					}
				}

			}
		});

		getDirectionControl().setDirection(Direction.Down);
	}

	@Override
	public void onRemove() {
		super.onRemove();
		removeController(state);
		removeController(ai);
	}

	private BehaviorNode getExampleAi() {
		return BehaviorTree.generateRoot(Gdx.files.internal("behavior/simpleEnemy.btree"));
	}

	private void createAnimations() {

		String moveupAnimation = "abomination_chase_up";
		String moveDownAnimation = "abomination_chase_down";
		String moveRightAnimation = "abomination_chase_right";
		String moveLeftAnimation = "abomination_chase_left";

		Animation<KyperSprite> moveUp = getState().getAnimation(moveupAnimation);
		if (moveUp == null) {
			getState().storeAnimation(moveupAnimation, getState().createGameAnimation(moveupAnimation, .12f));
		}
		Animation<KyperSprite> moveDown = getState().getAnimation(moveDownAnimation);
		if (moveDown == null) {
			getState().storeAnimation(moveDownAnimation, getState().createGameAnimation(moveDownAnimation, .12f));
		}

		Animation<KyperSprite> moveRight = getState().getAnimation(moveRightAnimation);
		if (moveRight == null) {
			getState().storeAnimation(moveRightAnimation, getState().createGameAnimation(moveRightAnimation, .12f));
		}
		Animation<KyperSprite> moveLeft = getState().getAnimation(moveLeftAnimation);
		if (moveLeft == null) {
			getState().storeAnimation(moveLeftAnimation, getState().createGameAnimation(moveLeftAnimation, .12f));
		}

		getAnimation().addAnimation("move_up", moveupAnimation);
		getAnimation().addAnimation("move_down", moveDownAnimation);
		getAnimation().addAnimation("move_right", moveRightAnimation);
		getAnimation().addAnimation("move_left", moveLeftAnimation);

	}
}
