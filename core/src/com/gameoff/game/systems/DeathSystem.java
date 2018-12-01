package com.gameoff.game.systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.objects.Collectible;
import com.gameoff.game.objects.composition.Lootable;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.objects.GameObject;
import com.gameoff.game.objects.enemies.SpiderBossEnemy;
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
			
			if(health == null)
				continue;

			if (health.isDead() == false)
			{
				if(health.shouldDie()) {
					//attempt to die
					health.setDead(health.attemptDeath(delta));
				}
			}

			if (o instanceof SpiderBossEnemy)
					continue;
			
			//if the object is dead then remove it
			if(health.isDead()) {
				if(KyperBoxGame.DEBUG_LOGGING)
					System.out.println(StringUtils.format("[%s] was considered dead and removed", o.getName()==null?"null":o.getName()));
				
				if(o instanceof Lootable) {
					Lootable l = (Lootable) o;
					int lootAmount = l.lootAmount();
					for (int j = 0; j < lootAmount; j++) {
						int loot = (l.loot());
						if(loot != Collectible.NONE) {
							Collectible item = Collectible.get(loot);
							Vector2 center = o.getCollisionCenter();
							item.setPosition(center.x, center.y);
							getLayer().addGameObject(item, KyperBoxGame.NULL_PROPERTIES);
						}
					}
				}
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
