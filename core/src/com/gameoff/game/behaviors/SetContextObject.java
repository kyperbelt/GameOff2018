package com.gameoff.game.behaviors;

import com.gameoff.game.Context;
import com.gameoff.game.GameOffGame;
import com.kyperbox.GameState;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.objects.GameLayer;
import com.kyperbox.objects.GameObject;
import com.kyperbox.umisc.StringUtils;

/**
 * set the object with the given name from the given layer into the given
 * context variable
 * 
 * @author john
 *
 */
public class SetContextObject extends BehaviorNode {

	private String objectName;
	private String layerName;
	private String contextName;

	private GameObject self; // needs a self to be able to get the current state

	public SetContextObject(String objectName, String layerName, String contextName) {
		this.objectName = objectName;
		this.layerName = layerName;
		this.contextName = contextName;
	}

	@Override
	public void init() {
		super.init();
		
		self = getContext().get(Context.SELF, GameObject.class);
	}

	@Override
	public NodeState update(float delta) {
		super.update(delta);
	
		if(self != null) {
			
			GameState state = self.getState();
			GameLayer layer = null;
			if(layerName.equalsIgnoreCase("uiground")) {
				layer = state.getUiLayer();
			}else if(layerName.equalsIgnoreCase("foregorund")) {
				layer = state.getForegroundLayer();
			}else if(layerName.equalsIgnoreCase("playground")) {
				layer = state.getPlaygroundLayer();
			}else if(layerName.equalsIgnoreCase("background")) {
				layer = state.getBackgroundLayer();
			}
			
			if(layer!=null) {
				
				GameObject o = layer.getGameObject(objectName);
				
				if(o!=null) {
					
					getContext().put(contextName, o);
					return NodeState.Success;
					
				}else {
					if(GameOffGame.DEBUG_LOGGING)
						System.out.println(StringUtils.format(getClass().getSimpleName()+": %s object not found in %s layer",objectName,layerName));
				}
				
				
			}else {
				if(GameOffGame.DEBUG_LOGGING)
					System.out.println(StringUtils.format(getClass().getSimpleName()+": %s layer not found in behavior",layerName));
			}
				
			
			
			
		}
		
		return NodeState.Failure;
	}

}
