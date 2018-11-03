package com.gameoff.game.control;

import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

public class AttackControl extends GameObjectController{

	AttackListener listener;
	
	public AttackControl(AttackListener attack) {
		this.listener = attack;
	}
	
	public void attack() {
		if(listener != null)
			listener.onAttack();
	}
	
	public void setAttackListener(AttackListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void init(GameObject object) {
		
	}

	@Override
	public void update(GameObject object, float delta) {
		
	}

	@Override
	public void remove(GameObject object) {
		
	}
	
	public interface AttackListener{
		public void onAttack();
	}

}
