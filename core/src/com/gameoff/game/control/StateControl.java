package com.gameoff.game.control;

import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

public class StateControl extends GameObjectController{
	
	public enum EntityState{
		Idling,Moving,Attacking,Dying, Damaged, Jumping, Dazed, Falling, Landing
	}
	
	EntityState state;
	
	StateChangeListener listener;
	
	public StateControl(EntityState state) {
		this.state = state;
	}
	
	public void setStateChangeListener(StateChangeListener listener) {
		this.listener = listener;
	}
	
	public EntityState getState() {
		return state;
	}
	
	public void setState(EntityState state) {
		
		if(listener!=null && this.state!=state)
			listener.stateChanged(this.state, state);
		this.state = state;
		
	}

	@Override
	public void init(GameObject object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameObject object, float delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(GameObject object) {
		// TODO Auto-generated method stub
		
	}
	
	public interface StateChangeListener{
		public void stateChanged(EntityState last,EntityState newState);
	}

}
