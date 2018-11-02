package com.gameoff.game.systems;

import com.badlogic.gdx.utils.Array;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.PlayerControl;
import com.kyperbox.input.GameInput;
import com.kyperbox.input.InputDefaults;
import com.kyperbox.objects.GameObject;
import com.kyperbox.systems.ControlSpecificSystem;
import com.kyperbox.umisc.StringUtils;

public class PlayerControlSystem extends ControlSpecificSystem {
	
	final String MAPS_NOT_FOUND = "no control mappaings found for id:[ %s ]";

	public static class PlayerControls {

		public String left;
		public String right;
		public String up;
		public String down;
		public String jump;
		public String attack;
		public String interact;

	}

	Array<PlayerControls> controls;

	public PlayerControlSystem() {
		super(PlayerControl.class);
		controls = new Array<PlayerControlSystem.PlayerControls>();
		PlayerControls p1 = new PlayerControls();
		p1.left = InputDefaults.MOVE_LEFT;
		p1.right = InputDefaults.MOVE_RIGHT;
		p1.up = InputDefaults.MOVE_UP;
		p1.down = InputDefaults.MOVE_DOWN;
		p1.jump = InputDefaults.JUMP_BUTTON;
		p1.attack = InputDefaults.ACTION_BUTTON;

		controls.add(p1);
	}

	@Override
	public void update(Array<GameObject> objects, float delta) {
		GameInput input = getLayer().getState().getInput();
		for (int i = 0; i < objects.size; i++) {
			GameObject o = objects.get(i);
			PlayerControl control = o.getController(PlayerControl.class);
			MoveControl move = o.getController(MoveControl.class);

			int id = control.getId();
			PlayerControls maps = null;

			if (id > -1 && id < controls.size) {
				maps = controls.get(id);
			} else {
				getLayer().getState().error(StringUtils.format(MAPS_NOT_FOUND, id));
			}
			
			if(maps!=null) {
				
				if(move!=null) {
					
					float x = 0;
					float y = 0;

					x -= input.inputValue(maps.left);
					x += input.inputValue(maps.right);

					y += input.inputValue(maps.up);
					y -= input.inputValue(maps.down);

					move.setDirection(x, y);
				}
				
			}
		}

	}

	@Override
	public void added(GameObject object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removed(GameObject object) {
		// TODO Auto-generated method stub

	}
}
