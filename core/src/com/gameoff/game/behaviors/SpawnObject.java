package com.gameoff.game.behaviors;

import com.gameoff.game.Context;
import com.gameoff.game.GameOffGame;
import com.kyperbox.GameState;
import com.kyperbox.KyperBoxGame;
import com.kyperbox.ai.BehaviorNode;
import com.kyperbox.ai.NodeState;
import com.kyperbox.objects.GameLayer;
import com.kyperbox.objects.GameObject;

/**
 * spawn an object at the given location on the given layer
 * 
 * @author john
 *
 */
public class SpawnObject extends BehaviorNode {

	private String objectType;
	private String layerName;
	private String relativeObject;

	private float x;
	private float y;

	private GameObject self;
	private GameObject spawnRelative;

	private GameLayer layer;

	public SpawnObject(String objectType, String layerName, String relativeObject, float x, float y) {
		this.objectType = objectType;
		this.layerName = layerName;
		this.relativeObject = relativeObject;
		this.x = x;
		this.y = y;
	}

	@Override
	public void init() {
		super.init();

		self = getContext().get(Context.SELF, GameObject.class);

		if (self != null) {
			GameState state = self.getState();
			if (layerName.equalsIgnoreCase("uiground")) {
				layer = state.getUiLayer();
			} else if (layerName.equalsIgnoreCase("foregorund")) {
				layer = state.getForegroundLayer();
			} else if (layerName.equalsIgnoreCase("playground")) {
				layer = state.getPlaygroundLayer();
			} else if (layerName.equalsIgnoreCase("background")) {
				layer = state.getBackgroundLayer();
			}
			if (relativeObject != null)
				spawnRelative = getContext().get(relativeObject, null, GameObject.class);
		}

	}

	@Override
	public NodeState update(float delta) {
		super.update(delta);

		if (layer != null) {
			GameOffGame game = (GameOffGame) layer.getState().getGame();

			GameObject o = game.getFactory().getGameObject(objectType);

			if (o != null) {

				if (spawnRelative != null) {
					o.setPosition(spawnRelative.getX() + x, spawnRelative.getY() + y);
				} else
					o.setPosition(x, y);

				layer.addGameObject(o, KyperBoxGame.NULL_PROPERTIES);

				return NodeState.Success;
			}

		}

		return NodeState.Failure;
	}

}
