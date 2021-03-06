package com.gameoff.game.systems;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.GameLevel;
import com.gameoff.game.GameOffGame;
import com.gameoff.game.Room;
import com.gameoff.game.control.PlayerControl;
import com.gameoff.game.managers.LevelManager;
import com.kyperbox.objects.GameObject;
import com.kyperbox.systems.ControlSpecificSystem;
import com.kyperbox.umisc.StringUtils;
import com.kyperbox.GameState;

/**
 * System to check if a player is out of bounds(has left the map bounds through
 * a door) to transition to the next room
 * 
 * @author john
 *
 */
public class OutOfBoundsSystem extends ControlSpecificSystem {

	Rectangle bounds;
	LevelManager level;

	public OutOfBoundsSystem(float x, float y, float width, float height) {
		super(PlayerControl.class);
		bounds = new Rectangle(x, y, width, height);
	}
	
	@Override
		public void init(MapProperties properties) {
			super.init(properties);
			GameOffGame game = (GameOffGame) getLayer().getState().getGame();
			level = game.getLevelManager();
		}

	@Override
	public void update(Array<GameObject> objects, float delta) {
		for (int i = 0; i < objects.size; i++) {
			GameObject player = objects.get(i);
			PlayerControl control = player.getController(PlayerControl.class);
			Rectangle prect = player.getCollisionBounds();

			// here we can use !bounds.overlaps(prect) if we want to wait until the player
			// is completely out of bounds
			if (!bounds.contains(prect)) {

				int dx = 0;
				int dy = 0;

				if (prect.x < bounds.x) {
					dx -= 1;
					level.setEntryPoint(LevelManager.EAST);
				} else if (prect.x + prect.width > bounds.x + bounds.width) {
					dx += 1;
					level.setEntryPoint(LevelManager.WEST);
				}

				if (prect.y < bounds.y) {
					dy += 1;
					level.setEntryPoint(LevelManager.NORTH);
				} else if (prect.y + prect.height > bounds.y + bounds.height) {
					dy -= 1;
					level.setEntryPoint(LevelManager.SOUTH);
				}

				GameLevel level = GameLevel.getCurrentLevel();
				Room cur = level.getCurrentRoom();
				GameState state = getLayer().getState();
				LevelManager lm = (LevelManager)(state.getManager());
				lm.saveRoomState(state, cur);

				System.out.println(StringUtils.format("currentRoom:(x[%s] y[%s]) | new dx[%s] dy[%s]", cur.getX(),
						cur.getY(), dx, dy));
				level.moveRoom(dx, dy);

				GameOffGame.log(this.getClass().getSimpleName(),
						StringUtils.format("Player[%s] is out of bounds. Moving to new Room[%s]", control.getId(),
								level.getCurrentRoom().getCode()));

				state.getGame().setGameState("room_" + level.getCurrentRoom().getCode());
			}
		}
	}

	@Override
	public void added(GameObject object) {
	}

	@Override
	public void removed(GameObject object) {
	}

}
