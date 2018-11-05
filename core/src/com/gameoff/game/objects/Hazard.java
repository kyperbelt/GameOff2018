package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.control.AttackControl;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.AttackControl.AttackListener;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;

public class Hazard extends Basic {

	AttackControl attack;
	
	Array<CollisionData> cols;
	
	AttackListener hazardListener = new AttackListener() {
		@Override
		public void onAttack() {
			for (int i = 0; i < cols.size; i++) {
				GameObject target = cols.get(i).getTarget();
				HealthControl health = target.getController(HealthControl.class);
				if(health!=null) {
					for (int j = 0; j < damageGroup.length; j++) {
						if(damageGroup[j] == health.getHealthGroup()) {
							health.changeCurrentHealth(-attack.getDamage()*attack.getDamageMult());
						}
						
					}
				}
			}
		}
	};
	
	HealthGroup[] damageGroup;

	public Hazard(HealthGroup... damageGroup) {
		this.damageGroup = damageGroup;
		getMove().setPhysical(false);
		attack = new AttackControl(1f,1f,hazardListener);
	}
	
	public Hazard() {
		this(HealthGroup.Angel,HealthGroup.Player,HealthGroup.Demon,HealthGroup.Neutral);
	}

	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		// hazard does not have health so we remove the health control so that systems
		// that interact with the health control do not affect it
		removeController(getHealth());
		addController(attack);
		
		setSprite("noregion");
	}
	
	
	
	@Override
	public void update(float delta) {
		super.update(delta);

		cols = getCollision().getCollisions();
		if(cols.size > 0) {
			attack.attack();
		}
	}

	@Override
	public void onRemove() {
		super.onRemove();
		removeController(attack);
	}

}
