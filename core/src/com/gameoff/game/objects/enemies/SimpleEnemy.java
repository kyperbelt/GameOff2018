package com.gameoff.game.objects.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.gameoff.game.Context;
import com.gameoff.game.control.AiControl;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.DirectionControl.DirectionChangeListener;
import com.gameoff.game.control.HealthControl.DamageListener;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.control.StateControl;
import com.gameoff.game.control.StateControl.EntityState;
import com.gameoff.game.control.StateControl.StateChangeListener;
import com.gameoff.game.objects.DirectionEntity;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.BehaviorTree;
import com.kyperbox.umisc.BakedEffects;
import com.kyperbox.umisc.KyperSprite;
import com.kyperbox.umisc.UserData;

public class SimpleEnemy extends EnemyEntity {

	StateControl state;
	String animation;
	UserData context;
	AiControl ai;

	float damagedDuration = .2f;
	float damagedElapsed = 0;
	ShaderProgram damageShader;
	
	Action shake = BakedEffects.shake(.5f, 10,false,false);

	DamageListener damageListener=new DamageListener(){

	@Override public void damaged(float amount){state.setState(EntityState.Damaged);damagedElapsed=0;}};

	StateChangeListener stateListener = new StateChangeListener() {

		@Override
		public void stateChanged(EntityState last, EntityState newState) {
			if (newState == EntityState.Damaged) {
				clearActions();
				shake.restart();
				addAction(shake);
				getAnimation().setPlaySpeed(0f);
			}else if(last == EntityState.Damaged){
				//no longer damaged
				clearActions();
				getAnimation().setPlaySpeed(1f);
			}
		}
	};

	public SimpleEnemy() {
		state = new StateControl(EntityState.Moving);
		context = new UserData(getClass().getSimpleName() + "_Context");
		context.put(Context.SELF, this);
		ai = new AiControl(context, getExampleAi());
		getMove().setMoveSpeed(80);
		getHealth().setHealthGroup(HealthGroup.Demon);
		getHealth().setDamageListener(damageListener);
		state.setStateChangeListener(stateListener);
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		addController(state);
		addController(ai);
		createAnimations();

		damageShader = getState().getShader("damageShader");

		if (getWidth() == 0) {
			setSize(32, 32);
			setCollisionBounds(0, 0, getWidth(), getHeight());
		}

		getDirectionControl().setDirectionListener(new DirectionChangeListener() {
			@Override
			public void directionChanged(Direction lastDirection, Direction newDirection) {
				if (state.getState() == EntityState.Moving || state.getState() == EntityState.Idling) {
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
	public void update(float delta) {

		if (state.getState() == EntityState.Damaged) {
			if (damagedElapsed >= damagedDuration) {
				state.setState(EntityState.Moving);
			}
			damagedElapsed += delta;
		}
		super.update(delta);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (state.getState() == EntityState.Damaged && damageShader != null) {
			ShaderProgram lastShader = batch.getShader();
			batch.setShader(damageShader);
			super.draw(batch, parentAlpha);
			batch.setShader(lastShader);
		} else {
			super.draw(batch, parentAlpha);
		}
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
