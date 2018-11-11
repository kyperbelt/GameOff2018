package com.gameoff.game.behaviors;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.Context;
import com.gameoff.game.control.PlayerControl;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.controllers.CollisionController;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.CollisionUtils;
import com.kyperbox.umisc.StringUtils;

/**
 * gets the nearest player in range
 * @author john
 *
 */
public class FindPlayerInRange extends BehaviorNode{

	
	float range;
	GameObject self;
	CollisionController cc;
	Rectangle bounds;
	Circle rangeCir;
	
	public FindPlayerInRange(float range) {
		this.range = range;
		bounds = new Rectangle(0,0,range,range);
		rangeCir = new Circle(0,0,range);
	}
	
	@Override
	public void init() {
		super.init();
		self = getContext().get(Context.SELF, GameObject.class);
		cc = self.getController(CollisionController.class);
		
		// if(KyperBoxGame.DEBUG_LOGGING)
		// System.out.println(StringUtils.format("[%s]:Finding Players in Range", self.getName()));
		
	}
	
	@Override
	public NodeState update(float delta) {
		super.update(delta);
		
		//no collision so we cant do the appropriate checks
		if(cc != null) {
			//get center of self
			Vector2 colCenter = self.getCollisionCenter();
			
			//place the bounds to the correct position
			bounds.x = colCenter.x - bounds.width*.5f;
			bounds.y = colCenter.y - bounds.height*.5f;
			
			//place the circle range to the correct position
			rangeCir.set(colCenter,range);
			
			//create a check object to use as the range check
			//TODO:put string creation in constructor
			GameObject check = CollisionUtils.getTestObject(bounds, self.getName()+"sight", self.getGroup()	, self.getFilter());
			
			//pass in the check object to the collisionControl and check for collisions
			Array<CollisionData> colData = cc.getCollisions(check, 0f);
			
			GameObject nearestPlayer = null;
			float nearest = range*2; //double the range
			
			for (int i = 0; i < colData.size; i++) {
				CollisionData data = colData.get(i);
				GameObject target = data.getTarget();
				PlayerControl control = target.getController(PlayerControl.class);
			
				if(control == null) //no player control means it is not a player
					continue;
				
				if(Intersector.overlaps(rangeCir, self.getCollisionBounds())) {
					
					float dist = colCenter.dst(target.getCollisionCenter());
					if(dist < nearest) {
						nearestPlayer = target;
						nearest = dist;
					}
				}
			}
			
			getContext().put(Context.PLAYER, nearestPlayer);
			if(nearestPlayer != null)
				return setState(NodeState.Success);
			
		}
		
		return setState(NodeState.Failure);
	}

}
