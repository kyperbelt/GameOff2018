package com.gameoff.game.control;

import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

public class PlayerControl extends GameObjectController{
	
	private int id;
	
	public PlayerControl(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
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

}
