package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class MeleeAttack extends Basic{
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
	
	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		setName("melee");
		getMove().setPhysical(false);
		setSprite("noregion");
		elapsed = 0;
	}
	
	@Override
	public void update(float delta) {

		super.update(delta);

		elapsed+=delta;
		if(elapsed>=lifetime) {
			remove();
		}
	}
	
	@Override
	public void onRemove() {
	
		used.removeValue(this, true);
		melees.free(this);
		super.onRemove();

	}
	
	public static MeleeAttack get() {
		MeleeAttack a = melees.obtain();
		used.add(a);
		return a;
	}
	
	public static void resetMeleeAttacks() {
		melees.freeAll(used);
	}

}
