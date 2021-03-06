package com.gameoff.game.systems;

import java.util.Comparator;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.gameoff.game.ZOrder;
import com.gameoff.game.control.ZOrderControl;
import com.gameoff.game.objects.BackgroundObject;
import com.kyperbox.objects.GameObject;
import com.gameoff.game.objects.Basic;
import com.gameoff.game.objects.Player;
import com.gameoff.game.objects.Spiker;
import com.kyperbox.objects.TilemapLayerObject;
import com.kyperbox.systems.LayerSystem;

public class YSortSystem extends LayerSystem {

	ZOrderControl defaultZOrder;
	
	public Comparator<Actor> ysort = new Comparator<Actor>() {
		@Override
		public int compare(Actor o1, Actor o2) {
			if (o1 instanceof GameObject && o2 instanceof GameObject) {
				GameObject go1 = (GameObject) o1;
				GameObject go2 = (GameObject) o2;

				ZOrderControl z1 = go1.getController(ZOrderControl.class);
				ZOrderControl z2 = go2.getController(ZOrderControl.class);
				
				z1  = z1 == null ? defaultZOrder : z1;
				z2  = z2 == null ? defaultZOrder : z2;

				if (z1 != null && z2 != null) {

					if (z1.getZOrder() > z2.getZOrder()) {
						return -1;
					} else if (z1.getZOrder() < z2.getZOrder()) {
						return 1;
					}else {
						//System.out.println("GOT HERE!");
						float y2 = o2.getY();
						if (o2 instanceof Basic)
						{
							Basic b2 = (Basic)o2;
							y2 += b2.getYOffset();
						}

						float y1 = o1.getY();
						if (o1 instanceof Basic)
						{
							Basic b1 = (Basic)o1;
							y1 += b1.getYOffset();
						}
						return Float.compare(y2, y1);
					}

				} else {
						float y2 = o2.getY();
						if (o2 instanceof Basic)
						{
							Basic b2 = (Basic)o2;
							y2 += b2.getYOffset();
						}
						float y1 = o1.getY();
						if (o1 instanceof Basic)
						{
							Basic b1 = (Basic)o1;
							y1 += b1.getYOffset();
						}
						return Float.compare(y2, y1);
				}

			}

			float y2 = o2.getY();
			if (o2 instanceof Basic)
			{
				Basic b2 = (Basic)o2;
				y2 += b2.getYOffset();
			}
			float y1 = o1.getY();
			if (o1 instanceof Basic)
			{
				Basic b1 = (Basic)o1;
				y1 += b1.getYOffset();
			}

			return Float.compare(y2, y1);
		}
	};

	@Override
	public void init(MapProperties properties) {
		defaultZOrder = new ZOrderControl();
		defaultZOrder.setZOrder(ZOrder.FLOOR_TEXT);
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
