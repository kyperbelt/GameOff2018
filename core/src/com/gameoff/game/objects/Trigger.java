package com.gameoff.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.Context;
import com.gameoff.game.control.AiControl;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.BehaviorTree;
import com.kyperbox.controllers.CollisionController;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.UserData;

public class Trigger extends GameObject {

	AiControl ai;
	UserData context;

	CollisionController collision;

	boolean persistent = false; // TODO: add some sort of persistence to this so that we dont trigger some
								// things on room return
	boolean triggered = false;

	public Trigger() {
		collision = new CollisionController();

	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		context = new UserData(getClass().getSimpleName() + "_" + getName() + "_context");
		context.put(Context.SELF, this);
		
		triggered = false;

		String rootFile = properties.get("triggerBehavior", KyperBoxGame.NULL_STRING, String.class);

		BehaviorNode root = null;

		if (!rootFile.equals(KyperBoxGame.NULL_STRING)) {
			root = BehaviorTree.generateRoot(Gdx.files.internal("behavior/" + rootFile));
		}

		ai = new AiControl(context, root);
		addController(collision);

	}

	@Override
	public void update(float delta) {
		super.update(delta);

		if (!triggered) {

			Array<CollisionData> colData = collision.getCollisions();

			for (int i = 0; i < colData.size; i++) {
				CollisionData data = colData.get(i);
				GameObject target = data.getTarget();

				// right now only triggers from players but we could make it trigger with others
				// as well later if thats something we want
				if (target instanceof Player) {

					addController(ai);// add the ai control so that it executes the behavior
					triggered = true;
					break;
				}
			}

		}

	}

	@Override
	public void onRemove() {
		super.onRemove();

		removeController(collision);
		removeController(ai);
	}

}
