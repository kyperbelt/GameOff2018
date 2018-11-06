package com.gameoff.game.systems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.GameLevel;
import com.gameoff.game.GameOffGame;
import com.gameoff.game.Room;
import com.gameoff.game.control.PlayerControl;
import com.kyperbox.objects.GameObject;
import com.kyperbox.systems.ControlSpecificSystem;
import com.kyperbox.umisc.StringUtils;

/**
 * System to check if a player is out of bounds(has left the map bounds through
 * a door) to transition to the next room
 * 
 * @author john
 *
 */
public class OutOfBoundsSystem extends ControlSpecificSystem {
	
	Rectangle bounds;

	public OutOfBoundsSystem(float x, float y, float width, float height) {
		super(PlayerControl.class);
		bounds = new Rectangle(x,y,width,height);
	}

	@Override
	public void update(Array<GameObject> objects, float delta) {
		for (int i = 0; i < objects.size; i++) {
			GameObject player = objects.get(i);
			PlayerControl control = player.getController(PlayerControl.class);
			Rectangle prect = player.getCollisionBounds();
			
			//here we can use !bounds.overlaps(prect) if we want to wait until the player is completely out of bounds
			if(!bounds.contains(prect)) {
				
				int dx = 0;
				int dy = 0;
				
				if(prect.x < bounds.x)
					dx-=1;
				if(prect.x+prect.width > bounds.x+bounds.width)
					dx+=1;
				if(prect.y < bounds.y)
					dy-=1;
				if(prect.y+prect.height > bounds.y+bounds.height)
					dy+=1;
				
				GameLevel level = GameLevel.getCurrentLevel();
				Room cur = level.getCurrentRoom();
				System.out.println(StringUtils.format("currentRoom:(x[%s] y[%s]) | new dx[%s] dy[%s]",cur.getX(),cur.getY(),dx,dy));
				level.moveRoom(dx, dy);
				
				GameOffGame.log(this.getClass().getSimpleName(), StringUtils.format("Player[%s] is out of bounds. Moving to new Room[%s]",control.getId(),level.getCurrentRoom().getCode()));
				
				getLayer().getState().getGame().setGameState("room_"+level.getCurrentRoom().getCode());
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
