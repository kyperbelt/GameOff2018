package com.gameoff.game.systems;

import com.badlogic.gdx.utils.Array;
import com.gameoff.game.control.AiControl;
import com.kyperbox.objects.GameObject;
import com.kyperbox.systems.ControlSpecificSystem;

public class AiSystem extends ControlSpecificSystem{

	public AiSystem() {
		super(AiControl.class);
	}

	@Override
	public void update(Array<GameObject> objects, float delta) {
		for (int i = 0; i < objects.size; i++) {
			GameObject o = objects.get(i);
			AiControl ai = o.getController(AiControl.class);
			ai.getTree().update(delta);
		}
	}

	@Override
	public void added(GameObject object) {
		AiControl ai = object.getController(AiControl.class);
		ai.getTree().start(ai.getContext(), ai.getRoot());
	}

	@Override
	public void removed(GameObject object) {
		
	}

}
