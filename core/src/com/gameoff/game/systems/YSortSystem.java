package com.gameoff.game.systems;

import java.util.Comparator;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.gameoff.game.control.ZOrderControl;
import com.gameoff.game.objects.*;
import com.kyperbox.objects.GameObject;
import com.kyperbox.objects.TilemapLayerObject;
import com.kyperbox.systems.LayerSystem;

public class YSortSystem extends LayerSystem{
	
	public Comparator<Actor> ysort = new Comparator<Actor>() {
		@Override
		public int compare(Actor o1, Actor o2) {
			
			
			if(o1 instanceof GameObject && o2 instanceof GameObject) {
				GameObject go1 = (GameObject) o1;
				GameObject go2 = (GameObject) o2;
				
				ZOrderControl z1 = go1.getController(ZOrderControl.class);
				ZOrderControl z2 = go2.getController(ZOrderControl.class);
				
				if(z1 !=null && z2 != null) {
					
					if(z1.getZOrder() > z2.getZOrder()) {
						return -1;
					}else if(z1.getZOrder() < z2.getZOrder()) {
						return 1;
					}
					
					
				}
				
			}
			
			
			
//			if(o1 instanceof TilemapLayerObject|| o2 instanceof TilemapLayerObject)
//				return 0;
//			if (o1 instanceof BackgroundObject)
//				return -1;
//			else if (o2 instanceof BackgroundObject)
//				return 1;
//			if(o1 instanceof Hazard) {
//				return -1;
//			}else if(o2 instanceof Hazard) {
//				return 1;
//			}
			
			
			if (o1 instanceof BackgroundObject)
				return -1;
			else if (o2 instanceof BackgroundObject)
				return 1;

			if (o1 instanceof ForegroundObject)
				return 1;
			else if (o2 instanceof ForegroundObject)
				return -1;
			
			if(o1 instanceof TilemapLayerObject) {
				return -1;
			}else if(o2 instanceof TilemapLayerObject) {
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
