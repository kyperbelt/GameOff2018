package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;

public class Projectile extends Attack {

	private static Pool<Projectile> projectiles = new Pool<Projectile>() {
		@Override
		protected Projectile newObject() {
			return new Projectile();
		}
	};
	
	private static Array<Projectile> used = new Array<Projectile>();

	float lifetime = 2f;
	float elapsed = 0;
	float damage = 1f;
	float speed = 300;
	
	boolean removeOnHit = true;
	
	Array<GameObject> attacked = new Array<GameObject>();
	
	public void setRemoveOnHit(boolean removeOnHit) {
		this.removeOnHit = removeOnHit;
	}
	
	public boolean isRemovedOnHit() {
		return removeOnHit;
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		setVelocity(0, 0);
		getHealth().setHealthGroup(HealthGroup.Projectile);
		setName("projectile");
		getMove().setMoveSpeed(speed);
		getMove().setPhysical(false);
		//setSprite("noregion");
		//setSize(49, 7);
		// must set the bounds of this object since it will have no bounds to begin with
		// since it is created dynamically. Therefore causing it to not collide
		//setBounds(0, 0, getWidth(), getHeight());
		elapsed = 0;
		attacked.clear();
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		
		
		boolean somethingHit = false;
		Array<CollisionData> colData = getCollision().getCollisions();
		for (int i = 0; i < colData.size; i++) {
			CollisionData data = colData.get(i);
			GameObject target = data.getTarget();
			HealthControl health = target.getController(HealthControl.class);
			
			if(health!=null) {
				HealthGroup group = health.getHealthGroup();
				if(!attacked.contains(target, true) && damages(group)) {
					health.changeCurrentHealth(-damage);
					attacked.add(target);
					somethingHit = true;
				}
			}
		}
		
		if(removeOnHit && somethingHit)
			remove();
		
		elapsed += delta;
		if (elapsed >= lifetime) {
			remove();
		}
	}

	@Override
	public void onRemove() {
		used.removeValue(this, true);
		projectiles.free(this);
		super.onRemove();
	}

	public static Projectile get(HealthGroup...damageGroup) {
		Projectile p = projectiles.obtain();
		p.setDamageGroup(damageGroup);
		used.add(p);
		return p;
	}

	public static void freeAll() {
		projectiles.freeAll(used);
	}

}
