package com.gameoff.game.systems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.control.MoveControl;
import com.kyperbox.controllers.CollisionController;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;
import com.kyperbox.systems.ControlSpecificSystem;

public class MoveSystem extends ControlSpecificSystem{

	float acum = 0;
	float step = 1f / 60f;
	
	public MoveSystem() {
		super(MoveControl.class);
	}

	@Override
	public void added(GameObject o) {
		
	}

	@Override
	public void removed(GameObject o) {
		
	}

	@Override
	public void update(Array<GameObject> o, float delta) {
		float ft = Math.min(delta, 0.25f);
		acum += ft;
		while (acum >= step) {
			updateMovement(o,step);
			acum -= step;
		}
	}
	
	public void updateMovement(Array<GameObject> objects,float delta) {
		for (int i = 0; i < objects.size; i++) {
			GameObject o = objects.get(i);
			//WE USE Controller/control interchangeably 
			//here we get the collision controller of the object
			CollisionController collision = o.getController(CollisionController.class);
			MoveControl move = o.getController(MoveControl.class);
			
			Vector2 vel = o.getVelocity();
			
			if(move != null) {
				vel.x = move.getMoveSpeed() * move.getXDir();
				vel.y = move.getMoveSpeed() * move.getYDir();
				vel.limit(move.getMoveSpeed());
				
				if(collision!=null) {
					Array<CollisionData> coldata = collision.getCollisions(delta);
					
					for (int j = 0; j < coldata.size; j++) {
						CollisionData cd = coldata.get(j);
						GameObject target = cd.getTarget();
						MoveControl target_move = target.getController(MoveControl.class);
					
						
						if(!move.isPhysical() || (target_move!=null && !target_move.isPhysical()))
							continue;
						
						if(move.isFlying() && (target_move!=null && target_move.isPassable()))
							continue;
						Rectangle overlap = cd.getOverlapBox();
						if (overlap.width < overlap.height) {

							if (vel.x < 0 && o.getBoundsX() + o.getBoundsRaw().width > target.getBoundsX()
									+ target.getBoundsRaw().width) {// commin in from the right
								o.setX(target.getBoundsX() + target.getBoundsRaw().width - o.getBoundsRaw().x);

								vel.x = 0;
							} else if (vel.x > 0 && o.getBoundsX() < target.getBoundsX()) {
								o.setX(target.getBoundsX() - (o.getBoundsRaw().x + o.getBoundsRaw().width));
								vel.x = 0;
							}
							
						} else
						// handle y axis
						if (overlap.width > overlap.height) {
							if (vel.y < 0 && o.getBoundsY() + o.getBoundsRaw().height > target.getBoundsY()
									+ target.getBoundsRaw().height) { // going down
								o.setY(target.getBoundsY() + target.getBoundsRaw().height - o.getBoundsRaw().y);
								
								vel.y = 0;
							} else if (vel.y > 0 && o.getBoundsY() < target.getBoundsY()) { // going up
								o.setY(target.getBoundsY() - (o.getBoundsRaw().y + o.getBoundsRaw().height) - 1);
								vel.y = 0;
							}
						}
					}
				}
			}
			
			o.setVelocity(vel.x,vel.y);
			o.setPosition(o.getX()+vel.x * delta, o.getY() + vel.y * delta);
		}
	}

}
