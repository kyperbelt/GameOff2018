package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class Projectile extends Basic {

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

	@Override
	public void init(MapProperties properties) {
		super.init(properties);
		setVelocity(0, 0);
		setName("projectile");
		getMove().setMoveSpeed(speed);
		getMove().setPhysical(false);
		setSprite("noregion");
		setSize(8, 8);
		// must set the bounds of this object since it will have no bounds to begin with
		// since it is created dynamically. Therefore causing it to not collide
		setBounds(0, 0, getWidth(), getHeight());
		elapsed = 0;

	}

	@Override
	public void update(float delta) {
		super.update(delta);
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

	public static Projectile get() {
		Projectile p = projectiles.obtain();
		used.add(p);
		return p;
	}

	public static void resetProjectiles() {
		projectiles.freeAll(used);
	}

}
