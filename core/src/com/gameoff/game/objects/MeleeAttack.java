package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;

public class MeleeAttack extends Attack{
	private static Pool<MeleeAttack> melees = new Pool<MeleeAttack>() {
		@Override
		protected MeleeAttack newObject() {
			return new MeleeAttack();
		}
	};
	
	private static Array<MeleeAttack> used = new Array<MeleeAttack>();

	float lifetime = .1f;
	float elapsed = 0;
	float damage = 2f;
	
	Array<GameObject> attacked = new Array<GameObject>();
	
	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		setName("melee");
		getMove().setPhysical(false);
		setSprite("noregion");
		getZOrder().setZOrder(ZOrder.PLAYER+1);
		elapsed = 0;
		attacked.clear();
	}
	
	@Override
	public void update(float delta) {

		super.update(delta);
		
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
				}
			}
		}

		elapsed+=delta;
		if(elapsed>=lifetime) {
			remove();
		}
	}

	public void setDamage(float d)
	{
		damage = d;
	}
	
	@Override
	public void onRemove() {
	
		used.removeValue(this, true);
		melees.free(this);
		attacked.clear();
		super.onRemove();

	}
	
	public static MeleeAttack get(HealthGroup...damageGroup) {
		MeleeAttack a = melees.obtain();
		a.setDamageGroup(damageGroup);
		used.add(a);
		return a;
	}
	
	public static void freeAll() {
		melees.freeAll(used);
	}

}
