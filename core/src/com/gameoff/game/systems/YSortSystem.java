package com.gameoff.game.systems;

import java.util.Comparator;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.gameoff.game.objects.Hazard;
import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.TilemapLayerObject;
import com.kyperbox.systems.LayerSystem;

public class YSortSystem extends LayerSystem{
	
	public Comparator<Actor> ysort = new Comparator<Actor>() {
		@Override
		public int compare(Actor o1, Actor o2) {
			if(o1 instanceof TilemapLayerObject|| o2 instanceof TilemapLayerObject)
				return 0;
			if(o1 instanceof Hazard) {
				return -1;
			}else if(o2 instanceof Hazard) {
				return 1;
			}
			return Float.compare(o2.getY(), o1.getY());
		}
	};
	
	@Override
	public void init(MapProperties properties) {
		
	}

	@Override
	public void gameObjectAdded(GameObject object, GameObject parent) {
		
	}

	@Override
	public void gameObjectChanged(GameObject object, int type, float value) {
		
	}

	@Override
	public void gameObjectRemoved(GameObject object, GameObject parent) {
		
	}

	@Override
	public void update(float delta) {
		getLayer().getChildren().sort(ysort);
	}

	@Override
	public void onRemove() {
	}


}
