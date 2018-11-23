package com.gameoff.game.objects;

import com.badlogic.gdx.maps.MapProperties;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.HealthControl;
import com.gameoff.game.control.HealthControl.HealthGroup;
import com.gameoff.game.control.MoveControl;
import com.gameoff.game.control.ZOrderControl;
import com.kyperbox.controllers.AnimationController;
import com.kyperbox.controllers.CollisionController;
import com.kyperbox.objects.GameObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gameoff.game.Context;
import com.gameoff.game.control.PlayerControl;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.controllers.CollisionController.CollisionData;
import com.kyperbox.umisc.CollisionUtils;
import com.kyperbox.umisc.StringUtils;

public class Basic extends GameObject {
	
	
	private HealthControl health;
	private CollisionController collision;
	private AnimationController animation;
	private MoveControl move;
	private ZOrderControl zorder;
	private float m_yOffset = 0;

	public Player m_playerTarget = null;
	public float m_distanceToPlayer = 99999;
	public Vector2 m_vectorToPlayer = new Vector2();

	String m_colName = null;
	Rectangle bounds;
	Circle rangeCir;
	float range = 0;
	
	
	public Basic() {
		collision = new CollisionController();
		animation = new AnimationController();
		health = new HealthControl(HealthGroup.Neutral,10f);
		move = new MoveControl(200);
		zorder = new ZOrderControl();
		zorder.setZOrder(ZOrder.PLAYER);
		setApplyVelocity(false);
		m_colName = this.getName()+"sight";
	}

	public void setPlayerFindRange(float r)
	{
		range = r;
		bounds = new Rectangle(0,0,range,range);
		rangeCir = new Circle(0,0,range);
	}

	public boolean setClosestPlayerData()
	{
		CollisionController cc = collision;

		if(cc != null) {
			//get center of self
			Vector2 colCenter = getCollisionCenter();
			
			//place the bounds to the correct position
			bounds.x = colCenter.x - bounds.width*.5f;
			bounds.y = colCenter.y - bounds.height*.5f;
			
			//place the circle range to the correct position
			rangeCir.set(colCenter,range);
			
			//create a check object to use as the range check
			//TODO:put string creation in constructor
			GameObject check = CollisionUtils.getTestObject(bounds, m_colName, getGroup(), getFilter());
			
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
				
				if(Intersector.overlaps(rangeCir, getCollisionBounds())) {
					float dist = colCenter.dst(target.getCollisionCenter());
					if(dist < nearest) {
						nearestPlayer = target;
						nearest = dist;
					}
				}
			}
			
			if(nearestPlayer != null)
			{
				m_playerTarget = (Player)nearestPlayer;
				m_distanceToPlayer = nearest;
				m_vectorToPlayer.set(m_playerTarget.getX() + m_playerTarget.getWidth()/2 - colCenter.x, m_playerTarget.getY() + m_playerTarget.getHeight()/2 - colCenter.y);
				return true;
			}
		}
		return false;
	}

	public void setYOffset(float yo)
	{
		m_yOffset = yo;
	}

	public float getYOffset()
	{
		return m_yOffset;
	}
	
	public ZOrderControl getZOrder() {
		return zorder;
	}
	
	/**
	 * get the collision controller for this object. 
	 * faster than using o.getController(Controller.class)
	 * @return
	 */
	public CollisionController getCollision() {
		return collision;
	}
	
	/**
	 * get the animation controller for this object. 
	 * Same as above
	 * @return
	 */
	public AnimationController getAnimation() {
		return animation;
	}
	
	public HealthControl getHealth() {
		return health;
	}
	
	/**
	 * get the move controller
	 * @return
	 */
	public MoveControl getMove() {
		return move;
	}

	/**
	 * this method initiates all game objects. we are able to get properties from
	 * tmx and use them in this init method. This method gets called every time a
	 * gameobject is added to a layer
	 */
	@Override
	public void init(MapProperties properties) {
		super.init(properties);

		addController(collision);
		addController(animation);
		addController(move);
		addController(health);
		addController(zorder);
	}

	@Override
	public void onRemove() {
		super.onRemove();
		removeController(collision);
		removeController(move);
		removeController(animation);
		removeController(health);
		removeController(zorder);
	}

}
