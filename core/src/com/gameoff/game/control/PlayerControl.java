package com.gameoff.game.control;

import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.Player.Form;
import com.gameoff.game.objects.Player.PlayerState;
import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.GameObjectController;

public class PlayerControl extends GameObjectController {

	private int id;
	private Player playerObject;

	public PlayerControl(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setForm(Form form) {

		if (playerObject != null) {
			if (playerObject.isTransforming() == false) {
				playerObject.transformTo(form);
				// playerObject.setCurrentForm(form);
			}
		}
	}

	public boolean isTransforming() {
		return playerObject.isTransforming();
	}

	public void setState(PlayerState state) {
		if (playerObject != null) {
			playerObject.setPlayerState(state);
		}
	}

	public PlayerState getState() {
		if (playerObject != null) {
			return playerObject.getPlayerState();
		}
		return PlayerState.Idling;
	}

	public boolean isDying() {
		if (playerObject != null) {
			return playerObject.isDying();
		}
		return false;
	}
	
	public Player getPlayer() {
		return playerObject;
	}

	@Override
	public void init(GameObject object) {
		this.playerObject = object instanceof Player ? (Player) object : null;
	}

	@Override
	public void update(GameObject object, float delta) {

	}

	@Override
	public void remove(GameObject object) {

	}

}
