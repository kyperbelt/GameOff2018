package com.gameoff.game.systems;

import com.badlogic.gdx.utils.Array;
import com.gameoff.game.Inputs;
import com.gameoff.game.Sounds;
import com.gameoff.game.control.AttackControl;
import com.gameoff.game.control.DirectionControl;
import com.gameoff.game.control.DirectionControl.Direction;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.PlayerControl;
import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.Player.Form;
import com.gameoff.game.objects.Player.PlayerState;
import com.kyperbox.input.GameInput;
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
		public String transform;
		public String attack;
		public String interact;
		public String dash;
		public String special;

	}

	Array<PlayerControls> controls;

	public PlayerControlSystem() {
		super(PlayerControl.class);
		controls = new Array<PlayerControlSystem.PlayerControls>();
		PlayerControls p1 = new PlayerControls();
		p1.left = Inputs.LEFT;
		p1.right = Inputs.RIGHT;
		p1.up = Inputs.UP;
		p1.down = Inputs.DOWN;
		p1.transform = Inputs.TRANSFORM;
		p1.attack = Inputs.ATTACK;
		p1.interact = Inputs.INTERACT;
		p1.dash = Inputs.DASH;
		p1.special = Inputs.SPECIAL;

		controls.add(p1);
	}

	@Override
	public void update(Array<GameObject> objects, float delta) {
		GameInput input = getLayer().getState().getInput();
		for (int i = 0; i < objects.size; i++) {
			GameObject o = objects.get(i);
			PlayerControl control = o.getController(PlayerControl.class);
			Player player = control.getPlayer();
			MoveControl move = o.getController(MoveControl.class);
			AttackControl attack = o.getController(AttackControl.class);
			DirectionControl direction = o.getController(DirectionControl.class);

			int id = control.getId();
			PlayerControls maps = null;

			if (id > -1 && id < controls.size) {
				maps = controls.get(id);
			} else {
				getLayer().getState().error(StringUtils.format(MAPS_NOT_FOUND, id));
			}

			// Can't move or do anything while transforming
			//if (control.isTransforming())
			//	return;

			if (maps != null) {

				if (control.getState() != PlayerState.Damaged) {

					if (move != null && !control.isDying()) {

						if (!control.isTransforming() && input.inputJustPressed(maps.transform)) {

							boolean transformed = false;

							if (move.isFlying()) {
								control.setForm(Form.Demon);
								transformed = true;
							} else if (player.m_numSouls >= player.angelFormMin) {
								control.setForm(Form.Angel);
								transformed = true;
							}

							if (transformed)
								getLayer().getState().playSound(Sounds.Transform);
						}

						if (input.inputJustPressed(maps.special)) {
							if (player.getCurrentForm() == Form.Angel) {
								player.setAngelShieldActive(!player.isAngelShieldActive());
							}else {
								player.setDemonFlameActive(!player.isDemonFlameActive());
							}
						}

						if (input.inputJustPressed(maps.attack)) {
							if (attack != null) {
								attack.resetCooldown();
								attack.attack();
								control.setState(PlayerState.Attacking);
							}
						} else if (input.inputPressed(maps.attack)) {
							if (attack != null) {
								attack.attack();
								control.setState(PlayerState.Attacking);
							}
						} else if (control.getState() == PlayerState.Attacking) {
							control.setState(PlayerState.Idling);
						}

						float x = 0;
						float y = 0;
						float threshold = .2f;

						x -= input.inputValue(maps.left) < threshold ? 0f : input.inputValue(maps.left);
						x += input.inputValue(maps.right) < threshold ? 0f : input.inputValue(maps.right);

						y += input.inputValue(maps.up) < threshold ? 0f : input.inputValue(maps.up);
						y -= input.inputValue(maps.down) < threshold ? 0f : input.inputValue(maps.down);

						if ((x != 0 || y != 0) && control.getState() == PlayerState.Idling)
							control.setState(PlayerState.Moving);
						else if (x == 0 && y == 0) {
							control.setState(PlayerState.Idling);
						}

						move.setDirection(x, y);

						if (direction != null && control.getState() != PlayerState.Attacking) {

							if (Math.abs(x) >= Math.abs(y)) {
								if (x < 0) {
									direction.setDirection(Direction.Left);
								} else if (x > 0) {
									direction.setDirection(Direction.Right);
								}
							} else {
								if (y < 0) {
									direction.setDirection(Direction.Down);
								} else if (y > 0) {
									direction.setDirection(Direction.Up);
								}

							}

						}

					}

					/// -----
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
