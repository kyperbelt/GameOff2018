package com.gameoff.game.systems;

import com.badlogic.gdx.utils.Array;
import com.gameoff.game.control.HealthControl;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.objects.GameObject;
import com.kyperbox.systems.ControlSpecificSystem;
import com.kyperbox.umisc.StringUtils;

public class DeathSystem extends ControlSpecificSystem {

	public DeathSystem() {
		super(HealthControl.class);
	}

	@Override
	public void update(Array<GameObject> objects, float delta) {
		for (int i = objects.size - 1; i >= 0; i--) {
			GameObject o = objects.get(i);
			HealthControl health = o.getController(HealthControl.class);
			
			//check if this object should die
			if(health.shouldDie()) {
				//attempt to die
				health.setDead(health.attemptDeath(delta));
			}
			
			//if the object is dead then remove it
			if(health.isDead()) {
				if(KyperBoxGame.DEBUG_LOGGING)
					System.out.println(StringUtils.format("[%s] was considered dead and removed", o.getName()==null?"null":o.getName()));
				o.remove();
			}
		}
	}

	@Override
	public void added(GameObject object) {

	}

	@Override
	public void removed(GameObject object) {

	}

}
