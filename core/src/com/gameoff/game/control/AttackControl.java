package com.gameoff.game.control;

import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

public class AttackControl extends GameObjectController{

	AttackListener listener;
	
	float cooldown = .35f; //cooldown used to time out auto fire
	float nelapsed = cooldown; //counts down instead of up- negative elapsed? lol
	
	public AttackControl(AttackListener attack) {
		this(.35f,attack);
	}
	
	public AttackControl(float cooldown,AttackListener attack) {
		this.listener = attack;
		this.cooldown = cooldown;
		resetCooldown();
	}
	
	public void resetCooldown() {
		nelapsed = cooldown;
	}
	
	public void setCooldown(float cooldown) {
		this.cooldown = cooldown;
		resetCooldown();
	}
	
	public float getCooldown() {
		return cooldown;
	}
	
	public void attack() {
		//if we have not met the cooldown 
		//then we simply dont attaack
		if(nelapsed < cooldown)
			return;
		if(listener != null)
			listener.onAttack();
		nelapsed = 0;
	}
	
	public void setAttackListener(AttackListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void init(GameObject object) {
		
	}

	@Override
	public void update(GameObject object, float delta) {
		
		//increment cooldown timer
		nelapsed+=delta;
	}

	@Override
	public void remove(GameObject object) {
		
	}
	
	public interface AttackListener{
		public void onAttack();
	}

}
