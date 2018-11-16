package com.gameoff.game.control;

import com.badlogic.gdx.math.MathUtils;
import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

public class HealthControl extends GameObjectController {

	public enum HealthGroup {
		Player, Angel, Demon, Neutral ,Projectile,Melee
	}

	float health = 10; // current health
	float max = 10; // max health
	boolean invulnerable = false;
	boolean isDead = false;

	HealthGroup group;

	DeathListener death;
	
	DamageListener damage;

	public HealthControl(HealthGroup group, float health) {
		setMaxHealth(health);
		setHealthGroup(group);
	}

	public void setHealthGroup(HealthGroup group) {
		this.group = group;
	}

	public HealthGroup getHealthGroup() {
		return group;
	}

	/**
	 * set if this control and its object are invulenrable - if true then their
	 * health cannot be lowered and they cannot be considered for death
	 * 
	 * @param invulnerable
	 */
	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	/**
	 * check if this control and its object are invulnerable
	 * 
	 * @return
	 */
	public boolean isInvulnerable() {
		return invulnerable;
	}
	
	public void setDamageListener(DamageListener damage) {
		this.damage = damage;
	}
	
	private void damage(float amount) {
		if(!invulnerable && amount > 0 && damage != null) {
			damage.damaged(amount);
		}
	}

	/**
	 * translate the current health by the given amount
	 * 
	 * @param amount
	 */
	public void changeCurrentHealth(float amount) {
		setCurrenHealth(getCurrentHealth() + amount);
	}

	/**
	 * set the current health of the control being invulnerable causes health to not
	 * lower
	 * 
	 * @param health
	 */
	public void setCurrenHealth(float health) {
		boolean shouldDamage = false;
		float lastHealth = this.health;
		if (health < this.health) {
			if(invulnerable)
				return;
			shouldDamage = true;
		}
		
		this.health = MathUtils.clamp(health, 0, max);
		
		if(shouldDamage)
			damage(lastHealth - health);
	}
	
	public float getHealthPercentage() {
		return getCurrentHealth() / getMaxHealth();
	}

	/**
	 * get the current health of the control
	 * 
	 * @return
	 */
	public float getCurrentHealth() {
		return health;
	}

	/**
	 * set the max health of this control, this automatically resets the current
	 * health to equal the max. TODO: add a flag so that we dont reset the curent
	 * health if we dont want to.
	 * 
	 * @param max
	 */
	public void setMaxHealth(float max) {
		this.max = Math.max(0f, max);
		setCurrenHealth(max);
	}

	/**
	 * get the current max healthof the control.
	 * 
	 * @return
	 */
	public float getMaxHealth() {
		return max;
	}

	/**
	 * checks to see if this control should be considered dead. This is only a
	 * consideration so that we can initiate logic before the control and its object
	 * are actually set to dead.
	 * 
	 * @return
	 */
	public boolean shouldDie() {
		return !invulnerable && getCurrentHealth() == 0;
	}

	/**
	 * set if this object is dead or not. Dead Controls(and their parent object)
	 * should be removed
	 * 
	 * @param dead
	 */
	public void setDead(boolean dead) {
		this.isDead = dead;
	}

	/**
	 * check if this control(object) are dead. Should be removed if true
	 * 
	 * @return
	 */
	public boolean isDead() {
		return isDead;
	}

	public void setDeathListener(DeathListener death) {
		this.death = death;
	}

	public boolean attemptDeath(float delta) {
		if (death != null)
			return death.die(delta);
		return true; // if there is no death listener death automatically succeeds.
	}

	@Override
	public void init(GameObject object) {
		setDead(false);
		setMaxHealth(max);
	}

	@Override
	public void update(GameObject object, float delta) {

	}

	public void remove(GameObject object) {

	}

	public interface DeathListener {
		public boolean die(float delta);
	}
	
	public interface DamageListener{
		public void damaged(float amount);
	}

}
